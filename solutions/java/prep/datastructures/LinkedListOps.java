package prep.datastructures;

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

    public static boolean hasCycle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    public static Node middle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

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
