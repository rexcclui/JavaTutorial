package prep.datastructures;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import prep.datastructures.TreeOps.TreeNode;

class TreeOpsTest {
    /**       4
     *      /   \
     *     2     6
     *    / \   / \
     *   1   3 5   7
     */
    private static TreeNode bst() {
        return new TreeNode(4,
                new TreeNode(2, new TreeNode(1), new TreeNode(3)),
                new TreeNode(6, new TreeNode(5), new TreeNode(7)));
    }

    // PURPOSE: Tree height counted in nodes; null tree has height 0.
    @Test
    void height() {
        assertThat(TreeOps.height(null)).isEqualTo(0);
        assertThat(TreeOps.height(bst())).isEqualTo(3);
    }

    // PURPOSE: In-order traversal of a BST yields sorted values; null gives an empty list.
    @Test
    void inorderOfBstIsSorted() {
        assertThat(TreeOps.inorder(bst())).containsExactly(1, 2, 3, 4, 5, 6, 7);
        assertThat(TreeOps.inorder(null)).isEmpty();
    }

    // PURPOSE: Breadth-first traversal returned level by level.
    @Test
    void levelOrder() {
        assertThat(TreeOps.levelOrder(bst())).containsExactly(List.of(4), List.of(2, 6), List.of(1, 3, 5, 7));
        assertThat(TreeOps.levelOrder(null)).isEmpty();
    }

    // PURPOSE: BST validation must check bounds from ALL ancestors (not only the parent), allow Integer.MAX_VALUE, and reject duplicates.
    @Test
    void validBst() {
        assertThat(TreeOps.isValidBst(bst())).isTrue();
        assertThat(TreeOps.isValidBst(null)).isTrue();
        // 5 is in the right subtree of the root (4)... but wait, it's a LEFT descendant here -> invalid
        TreeNode bad = new TreeNode(4, new TreeNode(2, new TreeNode(1), new TreeNode(5)), new TreeNode(6));
        assertThat(TreeOps.isValidBst(bad)).isFalse();
        assertThat(TreeOps.isValidBst(new TreeNode(Integer.MAX_VALUE))).isTrue();
        assertThat(TreeOps.isValidBst(new TreeNode(2, new TreeNode(2), null))).isFalse(); // duplicates not allowed
    }

    // PURPOSE: Lowest common ancestor in a BST by walking down using the ordering property.
    @Test
    void lcaBst() {
        assertThat(TreeOps.lcaBst(bst(), 1, 3).val).isEqualTo(2);
        assertThat(TreeOps.lcaBst(bst(), 1, 7).val).isEqualTo(4);
        assertThat(TreeOps.lcaBst(bst(), 2, 3).val).isEqualTo(2);
    }
}
