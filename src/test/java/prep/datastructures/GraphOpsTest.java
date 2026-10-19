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

    @Test
    void bfsOrder() {
        assertThat(GraphOps.bfs(G, 1)).containsExactly(1, 2, 3, 4, 5);
        assertThat(GraphOps.bfs(Map.of(), 9)).containsExactly(9);
    }

    @Test
    void dfsOrder() {
        assertThat(GraphOps.dfs(G, 1)).containsExactly(1, 2, 4, 5, 3);
    }

    @Test
    void shortestPath() {
        assertThat(GraphOps.shortestPath(G, 1, 5)).isEqualTo(3);
        assertThat(GraphOps.shortestPath(G, 1, 1)).isEqualTo(0);
        assertThat(GraphOps.shortestPath(Map.of(1, List.of(2), 3, List.of()), 1, 3)).isEqualTo(-1);
    }

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

    @Test
    void canFinish() {
        assertThat(GraphOps.canFinish(2, new int[][] {{1, 0}})).isTrue();
        assertThat(GraphOps.canFinish(2, new int[][] {{1, 0}, {0, 1}})).isFalse();
        assertThat(GraphOps.canFinish(4, new int[][] {{1, 0}, {2, 1}, {3, 2}})).isTrue();
    }
}
