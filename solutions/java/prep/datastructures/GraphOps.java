package prep.datastructures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GraphOps {

    public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> seen = new HashSet<>(List.of(start));
        Queue<Integer> q = new ArrayDeque<>(List.of(start));
        while (!q.isEmpty()) {
            int cur = q.poll();
            order.add(cur);
            for (int nb : graph.getOrDefault(cur, List.of())) {
                if (seen.add(nb)) q.add(nb);          // mark when ENQUEUED, not when dequeued
            }
        }
        return order;
    }

    public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        dfs(graph, start, new HashSet<>(), order);
        return order;
    }

    private static void dfs(Map<Integer, List<Integer>> g, int node, Set<Integer> seen, List<Integer> order) {
        if (!seen.add(node)) return;
        order.add(node);
        for (int nb : g.getOrDefault(node, List.of())) dfs(g, nb, seen, order);
    }

    public static int shortestPath(Map<Integer, List<Integer>> graph, int src, int dst) {
        if (src == dst) return 0;
        Set<Integer> seen = new HashSet<>(List.of(src));
        Queue<Integer> q = new ArrayDeque<>(List.of(src));
        int dist = 0;
        while (!q.isEmpty()) {
            dist++;
            for (int size = q.size(); size > 0; size--) {
                int cur = q.poll();
                for (int nb : graph.getOrDefault(cur, List.of())) {
                    if (nb == dst) return dist;
                    if (seen.add(nb)) q.add(nb);
                }
            }
        }
        return -1;
    }

    public static int numIslands(char[][] grid) {
        if (grid.length == 0) return 0;
        boolean[][] seen = new boolean[grid.length][grid[0].length];
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == '1' && !seen[r][c]) {
                    count++;
                    flood(grid, seen, r, c);
                }
            }
        }
        return count;
    }

    private static void flood(char[][] g, boolean[][] seen, int r, int c) {
        Deque<int[]> stack = new ArrayDeque<>();      // explicit stack: no StackOverflow on big grids
        stack.push(new int[] {r, c});
        while (!stack.isEmpty()) {
            int[] p = stack.pop();
            int i = p[0], j = p[1];
            if (i < 0 || j < 0 || i >= g.length || j >= g[0].length || g[i][j] != '1' || seen[i][j]) continue;
            seen[i][j] = true;
            stack.push(new int[] {i + 1, j});
            stack.push(new int[] {i - 1, j});
            stack.push(new int[] {i, j + 1});
            stack.push(new int[] {i, j - 1});
        }
    }

    /** Kahn's algorithm (topological sort by in-degree). */
    public static boolean canFinish(int n, int[][] prerequisites) {
        List<List<Integer>> next = new ArrayList<>();
        int[] indegree = new int[n];
        for (int i = 0; i < n; i++) next.add(new ArrayList<>());
        for (int[] p : prerequisites) {
            next.get(p[1]).add(p[0]);
            indegree[p[0]]++;
        }
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indegree[i] == 0) q.add(i);
        int done = 0;
        while (!q.isEmpty()) {
            int cur = q.poll();
            done++;
            for (int nb : next.get(cur)) if (--indegree[nb] == 0) q.add(nb);
        }
        return done == n;
    }
}
