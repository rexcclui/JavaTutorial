package prep.datastructures;

/** EXERCISE: singly linked list classics. */
public class LinkedListOps {
    public static class Node {
        public int val;
        public Node next;
        public Node(int val) { this.val = val; }
        public Node(int val, Node next) { this.val = val; this.next = next; }

        public static Node of(int... vals) {
            Node dummy = new Node(0), cur = dummy;
            for (int v : vals) { cur.next = new Node(v); cur = cur.next; }
            return dummy.next;
        }

        public int[] toArray() {
            java.util.List<Integer> out = new java.util.ArrayList<>();
            for (Node n = this; n != null; n = n.next) out.add(n.val);
            return out.stream().mapToInt(Integer::intValue).toArray();
        }
    }

    /** Reverse the list, return new head. Iterative, O(1) space. */
    public static Node reverse(Node head) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Floyd's tortoise and hare. */
    public static boolean hasCycle(Node head) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Middle node (second middle for even length). One pass. */
    public static Node middle(Node head) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Merge two sorted lists into one sorted list. */
    public static Node mergeSorted(Node a, Node b) {
        throw new UnsupportedOperationException("TODO");
    }
}
