package prep.design;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class RateLimiterTest {
    private static final long SECOND = 1_000_000_000L;

    @Test
    void startsFullThenRejects() {
        AtomicLong clock = new AtomicLong();
        RateLimiter rl = new RateLimiter(3, 1.0, clock::get);
        assertThat(rl.tryAcquire()).isTrue();
        assertThat(rl.tryAcquire()).isTrue();
        assertThat(rl.tryAcquire()).isTrue();
        assertThat(rl.tryAcquire()).isFalse();
    }

    @Test
    void refillsOverTime() {
        AtomicLong clock = new AtomicLong();
        RateLimiter rl = new RateLimiter(2, 2.0, clock::get);   // 2 tokens/sec
        rl.tryAcquire();
        rl.tryAcquire();
        assertThat(rl.tryAcquire()).isFalse();
        clock.addAndGet(SECOND / 2);                              // +1 token
        assertThat(rl.tryAcquire()).isTrue();
        assertThat(rl.tryAcquire()).isFalse();
    }

    @Test
    void neverExceedsCapacity() {
        AtomicLong clock = new AtomicLong();
        RateLimiter rl = new RateLimiter(2, 10.0, clock::get);
        clock.addAndGet(100 * SECOND);
        assertThat(rl.tryAcquire()).isTrue();
        assertThat(rl.tryAcquire()).isTrue();
        assertThat(rl.tryAcquire()).isFalse();
    }

    @Test
    void threadSafeNeverGrantsMoreThanCapacity() throws Exception {
        AtomicLong clock = new AtomicLong();   // frozen time: exactly `capacity` grants allowed
        RateLimiter rl = new RateLimiter(100, 1.0, clock::get);
        AtomicLong granted = new AtomicLong();
        Thread[] ts = new Thread[8];
        for (int i = 0; i < ts.length; i++) {
            ts[i] = prep.TestThreads.newThread(() -> {
                for (int j = 0; j < 100; j++) if (rl.tryAcquire()) granted.incrementAndGet();
            });
            ts[i].start();
        }
        for (Thread t : ts) t.join();
        assertThat(granted.get()).isEqualTo(100);
    }
}
