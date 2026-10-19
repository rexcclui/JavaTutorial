package prep.datastructures;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import prep.datastructures.LinkedListOps.Node;

class LinkedListOpsTest {
    // PURPOSE: Reverse a singly linked list: several nodes, a single node, and null.
    @Test
    void reverse() {
        assertThat(LinkedListOps.reverse(Node.of(1, 2, 3, 4)).toArray()).containsExactly(4, 3, 2, 1);
        assertThat(LinkedListOps.reverse(Node.of(1)).toArray()).containsExactly(1);
        assertThat(LinkedListOps.reverse(null)).isNull();
    }

    // PURPOSE: Floyd's tortoise-and-hare: false for a normal list, true after linking the tail back, false for null.
    @Test
    void hasCycle() {
        Node list = Node.of(1, 2, 3);
        assertThat(LinkedListOps.hasCycle(list)).isFalse();
        list.next.next.next = list.next;   // 3 -> 2
        assertThat(LinkedListOps.hasCycle(list)).isTrue();
        assertThat(LinkedListOps.hasCycle(null)).isFalse();
    }

    // PURPOSE: Slow/fast pointers find the middle (second middle for even length) in one pass.
    @Test
    void middle() {
        assertThat(LinkedListOps.middle(Node.of(1, 2, 3)).val).isEqualTo(2);
        assertThat(LinkedListOps.middle(Node.of(1, 2, 3, 4)).val).isEqualTo(3);
        assertThat(LinkedListOps.middle(Node.of(1)).val).isEqualTo(1);
    }

    // PURPOSE: Merge two sorted lists into one sorted list; one side may be empty.
    @Test
    void mergeSorted() {
        assertThat(LinkedListOps.mergeSorted(Node.of(1, 4, 6), Node.of(2, 3, 7)).toArray())
                .containsExactly(1, 2, 3, 4, 6, 7);
        assertThat(LinkedListOps.mergeSorted(null, Node.of(1)).toArray()).containsExactly(1);
    }
}
