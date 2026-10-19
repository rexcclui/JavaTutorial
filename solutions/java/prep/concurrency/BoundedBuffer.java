package prep.concurrency;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A thread-safe, fixed-size queue where producers wait when it is full and consumers wait when it is empty.
 *
 * How it works:
 *   1. All methods are synchronized on the buffer object, so only one thread changes it at a time.
 *   2. wait() releases the lock and sleeps; notifyAll() wakes the sleeping threads so they re-check their condition.
 */
public class BoundedBuffer<T> {
    private final Deque<T> items = new ArrayDeque<>();
    private final int capacity;

    /**
     * Creates a buffer holding at most 'capacity' items (must be > 0).
     *
     * Complexity: O(1)
     */
    public BoundedBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
    }

    /**
     * Adds an item at the back; blocks while the buffer is full.
     *
     * How it works:
     *   1. While the buffer is full: wait(). A 'while' (not 'if') is required because a thread can wake up and find the condition false again (another producer got in first, or a spurious wakeup).
     *   2. Add the item.
     *   3. notifyAll() so waiting consumers can continue.
     *
     * Complexity: O(1) plus waiting time
     */
    public synchronized void put(T item) throws InterruptedException {
        while (items.size() == capacity) wait();   // while, not if: guards against spurious wakeups
        items.addLast(item);
        notifyAll();
    }

    /**
     * Removes and returns the oldest item; blocks while the buffer is empty.
     *
     * How it works:
     *   1. While the buffer is empty: wait().
     *   2. Remove the first item.
     *   3. notifyAll() so waiting producers know there is room.
     *
     * Complexity: O(1) plus waiting time
     */
    public synchronized T take() throws InterruptedException {
        while (items.isEmpty()) wait();
        T item = items.removeFirst();
        notifyAll();
        return item;
    }

    /**
     * Number of items currently inside (synchronized so the read is consistent).
     *
     * Complexity: O(1)
     */
    public synchronized int size() {
        return items.size();
    }
    // TWIST: use ReentrantLock + two Conditions (notFull, notEmpty) so you can signal() instead of notifyAll().
}
