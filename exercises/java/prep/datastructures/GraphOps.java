package prep.datastructures;

import java.util.List;
import java.util.Map;

/** EXERCISE: graph traversal. */
public class GraphOps {

    /** BFS visit order from start; neighbours visited in list order. Unknown start -> just [start]. */
    public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        throw new UnsupportedOperationException("TODO");
    }

    /** DFS (preorder) visit order from start; neighbours in list order. */
    public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int start) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Shortest number of edges from src to dst in an unweighted graph, or -1 if unreachable. */
    public static int shortestPath(Map<Integer, List<Integer>> graph, int src, int dst) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Count connected groups of '1' in a grid (4-directional). Don't mutate the input. */
    public static int numIslands(char[][] grid) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Is there a valid order to take courses 0..n-1 given prerequisites [course, prereq]? (cycle detection) */
    public static boolean canFinish(int n, int[][] prerequisites) {
        throw new UnsupportedOperationException("TODO");
    }
}
