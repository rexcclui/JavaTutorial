package prep.datastructures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Graph algorithms on a graph stored as an adjacency list: Map from a node to the list of its neighbours.
 *
 * In the complexity notes, V = number of nodes (vertices) and E = number of edges (links between nodes).
 */
public class GraphOps {

    /**
     * Order in which breadth-first search visits nodes from start: nearest nodes first.
     *
     * How it works:
     *   1. Put start in a queue and in a 'seen' set.
     *   2. Repeatedly take the front node, record it, and add each unseen neighbour to the queue.
     *   3. A node is marked seen when it is ADDED to the queue, so it can never be queued twice (this also handles cycles).
     *
     * Complexity: time O(V + E), space O(V)
     */
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

    /**
     * Order in which depth-first search (pre-order) visits nodes from start: go as deep as possible first.
     *
     * How it works:
     *   1. Delegates to a recursive helper that shares the seen set and the output list.
     *
     * Complexity: time O(V + E), space O(V)
     */
    public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        dfs(graph, start, new HashSet<>(), order);
        return order;
    }

    /**
     * Recursive helper for dfs.
     *
     * How it works:
     *   1. If the node was already seen, stop (prevents infinite loops on cycles).
     *   2. Record the node, then run dfs on each neighbour in order.
     *
     * Complexity: time O(V + E)
     */
    private static void dfs(Map<Integer, List<Integer>> g, int node, Set<Integer> seen, List<Integer> order) {
        if (!seen.add(node)) return;
        order.add(node);
        for (int nb : g.getOrDefault(node, List.of())) dfs(g, nb, seen, order);
    }

    /**
     * Fewest edges between src and dst in an unweighted graph, or -1 if unreachable.
     *
     * How it works:
     *   1. BFS visits nodes in rounds: round 1 = nodes 1 edge away, round 2 = 2 edges away, and so on.
     *   2. Count the rounds ('dist'). The first time dst appears as a neighbour, dist is the answer.
     *   3. If the queue empties without finding dst, there is no path.
     *
     * Complexity: time O(V + E), space O(V)
     */
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

    /**
     * Number of separate groups of '1' cells in a grid; cells connect up, down, left and right.
     *
     * How it works:
     *   1. Scan every cell. When a '1' has not been visited yet, a NEW island starts: count it.
     *   2. Flood fill from that cell to mark every connected '1' as visited so it is not counted again.
     *   3. A separate 'seen' array is used, so the input grid is not modified.
     *
     * Complexity: time O(rows * cols), space O(rows * cols)
     */
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

    /**
     * Marks every land cell connected to (r, c) as visited.
     *
     * How it works:
     *   1. Use an explicit stack of cells instead of recursion, so a huge island cannot cause StackOverflowError.
     *   2. Pop a cell; skip it if it is outside the grid, is water, or was visited.
     *   3. Mark it visited and push its four neighbours.
     *
     * Complexity: O(cells in the island)
     */
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

    /**
     * True if all n courses can be completed given prerequisite pairs [course, prerequisite] (the graph has no cycle).
     *
     * How it works:
     *   1. Build, for each course, the list of courses that depend on it, and count each course's unmet prerequisites (in-degree).
     *   2. Start a queue with courses that have no prerequisites.
     *   3. Take a course, count it as done, and reduce the in-degree of the courses that depend on it; queue those that reach 0 (Kahn's algorithm / topological sort).
     *   4. If every course got done, there is no cycle; otherwise some courses wait on each other forever.
     *
     * Example: n=2, {{1,0}}  ->  true;  {{1,0},{0,1}}  ->  false
     *
     * Complexity: time O(V + E), space O(V + E)
     */
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
