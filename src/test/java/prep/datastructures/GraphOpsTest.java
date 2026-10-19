package prep.datastructures;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class GraphOpsTest {
    private static final Map<Integer, List<Integer>> G = Map.of(
            1, List.of(2, 3),
            2, List.of(4),
            3, List.of(4),
            4, List.of(5),
            5, List.of(1));   // cycle back to 1

    // PURPOSE: Breadth-first visit order on a graph with a cycle (no node visited twice); unknown start node gives just itself.
    @Test
    void bfsOrder() {
        assertThat(GraphOps.bfs(G, 1)).containsExactly(1, 2, 3, 4, 5);
        assertThat(GraphOps.bfs(Map.of(), 9)).containsExactly(9);
    }

    // PURPOSE: Depth-first (pre-order) visit order on the same graph.
    @Test
    void dfsOrder() {
        assertThat(GraphOps.dfs(G, 1)).containsExactly(1, 2, 4, 5, 3);
    }

    // PURPOSE: Fewest edges between two nodes using BFS; 0 for the same node, -1 when unreachable.
    @Test
    void shortestPath() {
        assertThat(GraphOps.shortestPath(G, 1, 5)).isEqualTo(3);
        assertThat(GraphOps.shortestPath(G, 1, 1)).isEqualTo(0);
        assertThat(GraphOps.shortestPath(Map.of(1, List.of(2), 3, List.of()), 1, 3)).isEqualTo(-1);
    }

    // PURPOSE: Count connected groups of land cells in a grid, without modifying the input grid.
    @Test
    void numIslands() {
        char[][] grid = {
            "11000".toCharArray(),
            "11000".toCharArray(),
            "00100".toCharArray(),
            "00011".toCharArray()};
        assertThat(GraphOps.numIslands(grid)).isEqualTo(3);
        assertThat(grid[0][0]).isEqualTo('1');   // input untouched
        assertThat(GraphOps.numIslands(new char[][] {})).isEqualTo(0);
    }

    // PURPOSE: Course scheduling = cycle detection in a directed graph (topological sort).
    @Test
    void canFinish() {
        assertThat(GraphOps.canFinish(2, new int[][] {{1, 0}})).isTrue();
        assertThat(GraphOps.canFinish(2, new int[][] {{1, 0}, {0, 1}})).isFalse();
        assertThat(GraphOps.canFinish(4, new int[][] {{1, 0}, {2, 1}, {3, 2}})).isTrue();
    }
}
