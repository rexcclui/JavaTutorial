package prep.algorithms;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ArrayTricksTest {
    // PURPOSE: Sliding window: longest substring with no repeated characters, including the tricky "abba" case.
    @Test
    void longestUniqueSubstring() {
        assertThat(ArrayTricks.longestUniqueSubstring("abcabcbb")).isEqualTo(3);
        assertThat(ArrayTricks.longestUniqueSubstring("bbbbb")).isEqualTo(1);
        assertThat(ArrayTricks.longestUniqueSubstring("pwwkew")).isEqualTo(3);
        assertThat(ArrayTricks.longestUniqueSubstring("abba")).isEqualTo(2);
        assertThat(ArrayTricks.longestUniqueSubstring("")).isEqualTo(0);
    }

    // PURPOSE: Fixed-size sliding window: best sum of k consecutive elements, including all-negative input.
    @Test
    void maxSumWindow() {
        assertThat(ArrayTricks.maxSumWindow(new int[] {2, 1, 5, 1, 3, 2}, 3)).isEqualTo(9);
        assertThat(ArrayTricks.maxSumWindow(new int[] {-1, -2, -3}, 2)).isEqualTo(-3);
    }

    // PURPOSE: Kadane's algorithm: best contiguous sum; with all negatives it must return the least negative number, not 0.
    @Test
    void maxSubarray() {
        assertThat(ArrayTricks.maxSubarray(new int[] {-2, 1, -3, 4, -1, 2, 1, -5, 4})).isEqualTo(6);
        assertThat(ArrayTricks.maxSubarray(new int[] {-3, -1, -2})).isEqualTo(-1);
    }

    // PURPOSE: Iterative Fibonacci: base cases and a large n that needs long (fib(90)).
    @Test
    void fib() {
        assertThat(ArrayTricks.fib(0)).isEqualTo(0);
        assertThat(ArrayTricks.fib(1)).isEqualTo(1);
        assertThat(ArrayTricks.fib(10)).isEqualTo(55);
        assertThat(ArrayTricks.fib(90)).isEqualTo(2880067194370816120L);
    }

    // PURPOSE: Dynamic programming: fewest coins for an amount, -1 when impossible, 0 for amount 0, plus a bigger case.
    @Test
    void coinChange() {
        assertThat(ArrayTricks.coinChange(new int[] {1, 2, 5}, 11)).isEqualTo(3);
        assertThat(ArrayTricks.coinChange(new int[] {2}, 3)).isEqualTo(-1);
        assertThat(ArrayTricks.coinChange(new int[] {1}, 0)).isEqualTo(0);
        assertThat(ArrayTricks.coinChange(new int[] {186, 419, 83, 408}, 6249)).isEqualTo(20);
    }
}
