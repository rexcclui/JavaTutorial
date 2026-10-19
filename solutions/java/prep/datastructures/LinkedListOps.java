package prep.datastructures;

/**
 * Singly linked list classics. A Node holds a value and a link to the next node.
 */
public class LinkedListOps {
    public static class Node {
        public int val;
        public Node next;
        public Node(int val) { this.val = val; }
        public Node(int val, Node next) { this.val = val; this.next = next; }

        /**
         * Test helper: builds a list from the given values.
         *
         * How it works:
         *   1. Start with a dummy node so there is always a 'current' to attach to.
         *   2. Append a new node per value and return dummy.next.
         *
         * Complexity: O(n)
         */
        public static Node of(int... vals) {
            Node dummy = new Node(0), cur = dummy;
            for (int v : vals) { cur.next = new Node(v); cur = cur.next; }
            return dummy.next;
        }

        /**
         * Test helper: reads the list from this node to the end into an int array.
         *
         * Complexity: O(n)
         */
        public int[] toArray() {
            java.util.List<Integer> out = new java.util.ArrayList<>();
            for (Node n = this; n != null; n = n.next) out.add(n.val);
            return out.stream().mapToInt(Integer::intValue).toArray();
        }
    }

    /**
     * Reverses the list in place and returns the new head.
     *
     * How it works:
     *   1. Keep 'prev' (already reversed part, starts null) and walk with 'head'.
     *   2. Save head.next, point head.next back at prev, then move prev and head one step forward.
     *   3. When head becomes null, prev is the new head.
     *
     * Example: 1 -> 2 -> 3  ->  3 -> 2 -> 1
     *
     * Complexity: time O(n), space O(1)
     */
    public static Node reverse(Node head) {
        Node prev = null;
        while (head != null) {
            Node next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    /**
     * True if following 'next' links ever loops back (Floyd's tortoise and hare).
     *
     * How it works:
     *   1. slow moves 1 step at a time, fast moves 2 steps.
     *   2. If there is no cycle, fast reaches the end (null).
     *   3. If there is a cycle, fast eventually laps slow and they meet at the same node.
     *
     * Complexity: time O(n), space O(1)
     */
    public static boolean hasCycle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    /**
     * The middle node (the second of the two middles when the length is even), in one pass.
     *
     * How it works:
     *   1. slow moves 1 step and fast moves 2 steps per round.
     *   2. When fast reaches the end, slow is at the middle.
     *
     * Example: 1 -> 2 -> 3 -> 4  ->  node 3
     *
     * Complexity: time O(n), space O(1)
     */
    public static Node middle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    /**
     * Merges two sorted lists into one sorted list by re-linking the existing nodes.
     *
     * How it works:
     *   1. A dummy head avoids special-casing the first node; 'tail' is where the next node is attached.
     *   2. Compare the front nodes of both lists and attach the smaller one to tail (<= keeps it stable).
     *   3. When one list runs out, attach the rest of the other list.
     *
     * Example: 1 -> 4 and 2 -> 3  ->  1 -> 2 -> 3 -> 4
     *
     * Complexity: time O(n + m), space O(1)
     */
    public static Node mergeSorted(Node a, Node b) {
        Node dummy = new Node(0), tail = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) { tail.next = a; a = a.next; }
            else { tail.next = b; b = b.next; }
            tail = tail.next;
        }
        tail.next = a != null ? a : b;
        return dummy.next;
        // TWIST: write reverse() recursively. What's the space cost, and when does it overflow the stack?
    }
}
