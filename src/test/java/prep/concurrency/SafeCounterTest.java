package prep.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.RepeatedTest;

class SafeCounterTest {
    // PURPOSE: 8 threads x 50,000 increments must total exactly 400,000. A plain count++ loses updates (race condition). Repeated 5 times to expose flakiness.
    @RepeatedTest(5)
    void noLostUpdates() throws Exception {
        SafeCounter counter = new SafeCounter();
        int threads = 8, perThread = 50_000;
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = prep.TestThreads.newThread(() -> {
                for (int j = 0; j < perThread; j++) counter.increment();
            });
            ts.add(t);
            t.start();
        }
        for (Thread t : ts) t.join();
        assertThat(counter.get()).isEqualTo(threads * perThread);
    }
}
