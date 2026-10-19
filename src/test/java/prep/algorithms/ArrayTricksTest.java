package prep.algorithms;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ArrayTricksTest {
    @Test
    void longestUniqueSubstring() {
        assertThat(ArrayTricks.longestUniqueSubstring("abcabcbb")).isEqualTo(3);
        assertThat(ArrayTricks.longestUniqueSubstring("bbbbb")).isEqualTo(1);
        assertThat(ArrayTricks.longestUniqueSubstring("pwwkew")).isEqualTo(3);
        assertThat(ArrayTricks.longestUniqueSubstring("abba")).isEqualTo(2);
        assertThat(ArrayTricks.longestUniqueSubstring("")).isEqualTo(0);
    }

    @Test
    void maxSumWindow() {
        assertThat(ArrayTricks.maxSumWindow(new int[] {2, 1, 5, 1, 3, 2}, 3)).isEqualTo(9);
        assertThat(ArrayTricks.maxSumWindow(new int[] {-1, -2, -3}, 2)).isEqualTo(-3);
    }

    @Test
    void maxSubarray() {
        assertThat(ArrayTricks.maxSubarray(new int[] {-2, 1, -3, 4, -1, 2, 1, -5, 4})).isEqualTo(6);
        assertThat(ArrayTricks.maxSubarray(new int[] {-3, -1, -2})).isEqualTo(-1);
    }

    @Test
    void fib() {
        assertThat(ArrayTricks.fib(0)).isEqualTo(0);
        assertThat(ArrayTricks.fib(1)).isEqualTo(1);
        assertThat(ArrayTricks.fib(10)).isEqualTo(55);
        assertThat(ArrayTricks.fib(90)).isEqualTo(2880067194370816120L);
    }

    @Test
    void coinChange() {
        assertThat(ArrayTricks.coinChange(new int[] {1, 2, 5}, 11)).isEqualTo(3);
        assertThat(ArrayTricks.coinChange(new int[] {2}, 3)).isEqualTo(-1);
        assertThat(ArrayTricks.coinChange(new int[] {1}, 0)).isEqualTo(0);
        assertThat(ArrayTricks.coinChange(new int[] {186, 419, 83, 408}, 6249)).isEqualTo(20);
    }
}
