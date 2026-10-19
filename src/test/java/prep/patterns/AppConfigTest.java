package prep.patterns;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

class AppConfigTest {
    // PURPOSE: Singleton: repeated getInstance() calls return the very same object.
    @Test
    void sameInstanceEveryTime() {
        assertThat(AppConfig.getInstance()).isSameAs(AppConfig.getInstance());
    }

    // PURPOSE: Singleton: nobody else may construct instances, so the constructor must not be public.
    @Test
    void constructorIsNotPublic() {
        assertThat(AppConfig.class.getConstructors()).isEmpty();
        assertThat(Modifier.isPublic(AppConfig.class.getDeclaredConstructors()[0].getModifiers())).isFalse();
    }

    // PURPOSE: Singleton under concurrency: 32 threads released at the same moment must all get the same instance.
    @Test
    void sameInstanceAcrossThreads() throws Exception {
        Set<AppConfig> seen = ConcurrentHashMap.newKeySet();
        int n = 32;
        CountDownLatch start = new CountDownLatch(1), done = new CountDownLatch(n);
        for (int i = 0; i < n; i++) {
            prep.TestThreads.newThread(() -> {
                try { start.await(); } catch (InterruptedException ignored) { }
                seen.add(AppConfig.getInstance());
                done.countDown();
            }).start();
        }
        start.countDown();
        done.await();
        assertThat(seen).hasSize(1);
    }
}
