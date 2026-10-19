package prep.datastructures;

import java.util.List;

/** EXERCISE: binary tree classics. Try both recursive and iterative versions. */
public class TreeOps {
    public static class TreeNode {
        public int val;
        public TreeNode left, right;
        public TreeNode(int val) { this.val = val; }
        public TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /** Number of nodes on the longest root-to-leaf path. null -> 0. */
    public static int height(TreeNode root) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Left, node, right. */
    public static List<Integer> inorder(TreeNode root) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Level by level, left to right. */
    public static List<List<Integer>> levelOrder(TreeNode root) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Is this a valid binary SEARCH tree? (every node bounds ALL its descendants, not only children) */
    public static boolean isValidBst(TreeNode root) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Lowest common ancestor of nodes with values p and q in a BST. */
    public static TreeNode lcaBst(TreeNode root, int p, int q) {
        throw new UnsupportedOperationException("TODO");
    }
}
