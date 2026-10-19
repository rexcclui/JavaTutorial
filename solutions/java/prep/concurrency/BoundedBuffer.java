package prep.concurrency;

import java.util.ArrayDeque;
import java.util.Deque;

public class BoundedBuffer<T> {
    private final Deque<T> items = new ArrayDeque<>();
    private final int capacity;

    public BoundedBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (items.size() == capacity) wait();   // while, not if: guards against spurious wakeups
        items.addLast(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (items.isEmpty()) wait();
        T item = items.removeFirst();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return items.size();
    }
    // TWIST: use ReentrantLock + two Conditions (notFull, notEmpty) so you can signal() instead of notifyAll().
}
