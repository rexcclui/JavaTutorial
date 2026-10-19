package prep.concurrency;

/**
 * EXERCISE: this counter loses updates under concurrency (count++ is read-modify-write).
 * Make increment() thread-safe. Try 3 ways: synchronized, AtomicInteger/LongAdder, ReentrantLock.
 * Then think: which is fastest under contention, and why?
 */
public class SafeCounter {
    private int count;

    public void increment() {
        count++;
    }

    public int get() {
        return count;
    }
}
