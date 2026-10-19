package prep.collections;

import java.util.HashMap;
import java.util.Map;

/** HashMap + doubly linked list with sentinel head/tail. All operations O(1). */
public class LruCache<K, V> {
    private final class Node {
        K key;
        V value;
        Node prev, next;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<K, Node> map = new HashMap<>();
    private final Node head = new Node(null, null); // most recent after head
    private final Node tail = new Node(null, null); // least recent before tail

    public LruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        Node n = map.get(key);
        if (n == null) return null;
        unlink(n);
        addFirst(n);
        return n.value;
    }

    public void put(K key, V value) {
        Node n = map.get(key);
        if (n != null) {
            n.value = value;
            unlink(n);
            addFirst(n);
            return;
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;
            unlink(lru);
            map.remove(lru.key);
        }
        n = new Node(key, value);
        map.put(key, n);
        addFirst(n);
    }

    public int size() { return map.size(); }

    private void unlink(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void addFirst(Node n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }
    // TWIST (Level 2): make it thread-safe. Which is better here: synchronized methods or ReentrantLock? Why?
    // TWIST (Level 3): add a TTL per entry.
}
