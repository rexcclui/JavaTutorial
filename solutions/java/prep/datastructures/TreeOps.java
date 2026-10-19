package prep.datastructures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TreeOps {
    public static class TreeNode {
        public int val;
        public TreeNode left, right;
        public TreeNode(int val) { this.val = val; }
        public TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    public static int height(TreeNode root) {
        return root == null ? 0 : 1 + Math.max(height(root.left), height(root.right));
    }

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

    public static boolean isValidBst(TreeNode root) {
        return valid(root, Long.MIN_VALUE, Long.MAX_VALUE);   // long bounds: node values can be Integer.MIN/MAX
    }

    private static boolean valid(TreeNode n, long lo, long hi) {
        if (n == null) return true;
        if (n.val <= lo || n.val >= hi) return false;
        return valid(n.left, lo, n.val) && valid(n.right, n.val, hi);
    }

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
