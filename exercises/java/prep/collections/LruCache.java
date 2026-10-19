package prep.collections;

/**
 * EXERCISE: Least-Recently-Used cache with O(1) get and put.
 * - get(key) returns null if absent; a hit makes the key most-recently-used.
 * - put(key, value) inserts/updates; when size exceeds capacity, evict the least-recently-used key.
 * Hint: LinkedHashMap(accessOrder=true) is the shortcut; HashMap + doubly linked list is the "real" answer.
 */
public class LruCache<K, V> {
    public LruCache(int capacity) {
        // TODO
    }

    public V get(K key) {
        throw new UnsupportedOperationException("TODO");
    }

    public void put(K key, V value) {
        throw new UnsupportedOperationException("TODO");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO");
    }
}
