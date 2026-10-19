package prep.datastructures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Binary tree classics: height, traversals, BST validation and lowest common ancestor.
 */
public class TreeOps {
    public static class TreeNode {
        public int val;
        public TreeNode left, right;
        public TreeNode(int val) { this.val = val; }
        public TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /**
     * Number of nodes on the longest path from the root down to a leaf (an empty tree is 0).
     *
     * How it works:
     *   1. An empty tree (null) has height 0.
     *   2. Otherwise 1 (this node) + the larger of the left and right subtree heights.
     *
     * Complexity: time O(n), space O(h) recursion depth
     */
    public static int height(TreeNode root) {
        return root == null ? 0 : 1 + Math.max(height(root.left), height(root.right));
    }

    /**
     * Values in left-node-right order; for a BST that is sorted order.
     *
     * How it works:
     *   1. Use an explicit stack instead of recursion.
     *   2. Go as far left as possible, pushing every node on the way.
     *   3. Pop a node, record its value, then move to its right child and repeat.
     *
     * Complexity: time O(n), space O(h)
     */
    public static List<Integer> inorder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        java.util.Deque<TreeNode> stack = new ArrayDeque<>();   // iterative version
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) { stack.push(cur); cur = cur.left; }
            cur = stack.pop();
            out.add(cur.val);
            cur = cur.right;
        }
        return out;
    }

    /**
     * Values level by level, left to right (breadth-first search).
     *
     * How it works:
     *   1. Put the root in a queue.
     *   2. For each round, take exactly queue.size() nodes: those are one level.
     *   3. Record their values and add their children to the queue for the next round.
     *
     * Example: a tree 4 / (2, 6)  ->  [[4], [2, 6]]
     *
     * Complexity: time O(n), space O(width)
     */
    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> out = new ArrayList<>();
        if (root == null) return out;
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            int size = q.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode n = q.poll();
                level.add(n.val);
                if (n.left != null) q.add(n.left);
                if (n.right != null) q.add(n.right);
            }
            out.add(level);
        }
        return out;
    }

    /**
     * True if the tree is a valid binary SEARCH tree.
     *
     * How it works:
     *   1. Every node must lie strictly between a lower and an upper bound that come from ALL its ancestors, not just its parent.
     *   2. Start with bounds of minus and plus infinity (long, so Integer.MIN/MAX values still work).
     *   3. Going left, the upper bound becomes the node's value; going right, the lower bound does. Duplicates are not allowed.
     *
     * Complexity: time O(n), space O(h)
     */
    public static boolean isValidBst(TreeNode root) {
        return valid(root, Long.MIN_VALUE, Long.MAX_VALUE);   // long bounds: node values can be Integer.MIN/MAX
    }

    /**
     * Helper for isValidBst: checks one node against (lo, hi) and recurses into both children with tighter bounds.
     *
     * Complexity: O(n) in total
     */
    private static boolean valid(TreeNode n, long lo, long hi) {
        if (n == null) return true;
        if (n.val <= lo || n.val >= hi) return false;
        return valid(n.left, lo, n.val) && valid(n.right, n.val, hi);
    }

    /**
     * Lowest common ancestor of values p and q in a BST.
     *
     * How it works:
     *   1. Start at the root.
     *   2. If both values are smaller than the current node, the answer is in the left subtree; if both are larger, go right.
     *   3. Otherwise the two values split here (or one equals this node), so this node is the lowest common ancestor.
     *
     * Complexity: time O(h), space O(1)
     */
    public static TreeNode lcaBst(TreeNode root, int p, int q) {
        TreeNode cur = root;
        while (cur != null) {
            if (p < cur.val && q < cur.val) cur = cur.left;
            else if (p > cur.val && q > cur.val) cur = cur.right;
            else return cur;
        }
        return null;
    }
}
