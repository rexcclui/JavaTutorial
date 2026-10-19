package prep.collections;

import java.util.HashMap;
import java.util.Map;

/**
 * A Least-Recently-Used cache with O(1) get and put.
 *
 * How it works:
 *   1. A HashMap finds an entry by key in O(1).
 *   2. A doubly linked list keeps entries ordered by recency: most recent right after 'head', least recent right before 'tail'.
 *   3. Each map value is the list node itself, so a node can be unlinked and moved to the front in O(1).
 *   4. head and tail are dummy sentinel nodes, which removes the 'is the list empty / is this the first node' special cases.
 */
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

    /**
     * Creates an empty cache that holds at most 'capacity' entries.
     *
     * How it works:
     *   1. Reject capacity <= 0.
     *   2. Link the two sentinels together: head <-> tail (an empty list).
     *
     * Complexity: O(1)
     */
    public LruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Returns the value for the key, or null if absent. A hit also marks the entry as most recently used.
     *
     * How it works:
     *   1. Look up the node in the map; if missing return null.
     *   2. Unlink the node from where it is and add it right after head (most recent position).
     *   3. Return its value.
     *
     * Complexity: O(1)
     */
    public V get(K key) {
        Node n = map.get(key);
        if (n == null) return null;
        unlink(n);
        addFirst(n);
        return n.value;
    }

    /**
     * Inserts or updates a key, evicting the least recently used entry when the cache is full.
     *
     * How it works:
     *   1. If the key already exists: update its value and move it to the front, then stop.
     *   2. If the cache is full: the least recent node is the one just before tail; unlink it and remove its key from the map.
     *   3. Create a new node, store it in the map and add it at the front.
     *
     * Complexity: O(1)
     */
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

    /**
     * Number of entries currently stored.
     *
     * Complexity: O(1)
     */
    public int size() { return map.size(); }

    /**
     * Removes a node from the linked list by joining its neighbours together (the node itself is not deleted from the map).
     *
     * How it works:
     *   1. Make the previous node point forward to the next node.
     *   2. Make the next node point back to the previous node.
     *
     * Complexity: O(1)
     */
    private void unlink(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    /**
     * Inserts a node directly after head, making it the most recently used.
     *
     * How it works:
     *   1. Point the node's next at what head used to point to, and its prev at head.
     *   2. Update the old first node's prev and head's next to point to the new node.
     *
     * Complexity: O(1)
     */
    private void addFirst(Node n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }
    // TWIST (Level 2): make it thread-safe. Which is better here: synchronized methods or ReentrantLock? Why?
    // TWIST (Level 3): add a TTL per entry.
}
