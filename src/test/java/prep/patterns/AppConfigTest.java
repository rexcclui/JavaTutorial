package prep.patterns;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

class AppConfigTest {
    @Test
    void sameInstanceEveryTime() {
        assertThat(AppConfig.getInstance()).isSameAs(AppConfig.getInstance());
    }

    @Test
    void constructorIsNotPublic() {
        assertThat(AppConfig.class.getConstructors()).isEmpty();
        assertThat(Modifier.isPublic(AppConfig.class.getDeclaredConstructors()[0].getModifiers())).isFalse();
    }

    @Test
    void sameInstanceAcrossThreads() throws Exception {
        Set<AppConfig> seen = ConcurrentHashMap.newKeySet();
        int n = 32;
        CountDownLatch start = new CountDownLatch(1), done = new CountDownLatch(n);
        for (int i = 0; i < n; i++) {
            new Thread(() -> {
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
