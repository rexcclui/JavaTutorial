package prep.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class SafeCounter {
    private final AtomicInteger count = new AtomicInteger();

    public void increment() {
        count.incrementAndGet();
    }

    public int get() {
        return count.get();
    }
    // TWIST: swap AtomicInteger for `synchronized` methods, then for a plain volatile int. What happens to the test with volatile?
}
