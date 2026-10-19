package prep.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A counter that stays correct when many threads call increment() at the same time.
 *
 * How it works:
 *   1. A plain 'count++' is three steps (read, add, write), so two threads can overwrite each other's update.
 *   2. AtomicInteger performs the whole increment as one indivisible hardware operation.
 */
public class SafeCounter {
    private final AtomicInteger count = new AtomicInteger();

    /**
     * Adds 1 atomically.
     *
     * How it works:
     *   1. incrementAndGet() uses compare-and-swap (CAS): retry until no other thread changed the value in between.
     *
     * Complexity: O(1), lock-free
     */
    public void increment() {
        count.incrementAndGet();
    }

    /**
     * Current count.
     *
     * Complexity: O(1)
     */
    public int get() {
        return count.get();
    }
    // TWIST: swap AtomicInteger for `synchronized` methods, then for a plain volatile int. What happens to the test with volatile?
}
