package prep.concurrency;

/**
 * EXERCISE: classic producer/consumer. A blocking bounded queue.
 * - put(): blocks while the buffer is full
 * - take(): blocks while the buffer is empty
 * Implement with wait/notifyAll (or Lock + Condition). Do NOT use java.util.concurrent.BlockingQueue.
 * Remember: always wait() inside a while loop, never an if. Why?
 */
public class BoundedBuffer<T> {
    public BoundedBuffer(int capacity) {
        // TODO
    }

    public void put(T item) throws InterruptedException {
        throw new UnsupportedOperationException("TODO");
    }

    public T take() throws InterruptedException {
        throw new UnsupportedOperationException("TODO");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO");
    }
}
