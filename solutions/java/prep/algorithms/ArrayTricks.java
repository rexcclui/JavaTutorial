package prep.algorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Sliding window, Kadane and dynamic programming warm-ups.
 */
public class ArrayTricks {

    /**
     * Length of the longest piece of the string with no repeated character.
     *
     * How it works:
     *   1. Keep a window [start, i] and a map from character to the last index it was seen at.
     *   2. Move i forward. If the current character was already seen inside the window, move start to just after that earlier position.
     *   3. Record the last index of the character and update the best window length i - start + 1.
     *
     * Example: "abcabcbb"  ->  3  ("abc")
     *
     * Complexity: time O(n), space O(k) distinct characters
     */
    public static int longestUniqueSubstring(String s) {
        Map<Character, Integer> last = new HashMap<>();
        int best = 0, start = 0;
        for (int i = 0; i < s.length(); i++) {
            Integer prev = last.put(s.charAt(i), i);
            if (prev != null && prev >= start) start = prev + 1;
            best = Math.max(best, i - start + 1);
        }
        return best;
    }

    /**
     * Largest sum of any k consecutive elements.
     *
     * How it works:
     *   1. Add up the first k elements.
     *   2. Slide the window one step: add the new element on the right and subtract the element that left on the left.
     *   3. Track the maximum sum seen. Each step is O(1) instead of re-adding k numbers.
     *
     * Example: {2, 1, 5, 1, 3, 2}, k=3  ->  9  (5 + 1 + 3)
     *
     * Complexity: time O(n), space O(1)
     */
    public static int maxSumWindow(int[] a, int k) {
        int sum = 0;
        for (int i = 0; i < k; i++) sum += a[i];
        int best = sum;
        for (int i = k; i < a.length; i++) {
            sum += a[i] - a[i - k];
            best = Math.max(best, sum);
        }
        return best;
    }

    /**
     * Largest sum of any non-empty contiguous part of the array (Kadane's algorithm).
     *
     * How it works:
     *   1. cur = best sum of a subarray that ENDS at the current element.
     *   2. At each element choose: start fresh here (a[i]) or extend the previous subarray (cur + a[i]), whichever is larger.
     *   3. best = the largest cur seen. Starting from a[0] (not 0) keeps all-negative arrays correct.
     *
     * Example: {-2, 1, -3, 4, -1, 2, 1, -5, 4}  ->  6  (4, -1, 2, 1)
     *
     * Complexity: time O(n), space O(1)
     */
    public static int maxSubarray(int[] a) {
        int best = a[0], cur = a[0];
        for (int i = 1; i < a.length; i++) {
            cur = Math.max(a[i], cur + a[i]);
            best = Math.max(best, cur);
        }
        return best;
    }

    /**
     * n-th Fibonacci number (0, 1, 1, 2, 3, 5, 8, ...).
     *
     * How it works:
     *   1. Only the previous two values are needed, so keep two variables a and b.
     *   2. n times: the new value is a + b; shift (a, b) to (b, a + b).
     *   3. long is used because fib(90) no longer fits in an int.
     *
     * Example: fib(10)  ->  55
     *
     * Complexity: time O(n), space O(1)
     */
    public static long fib(int n) {
        long a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            long t = a + b;
            a = b;
            b = t;
        }
        return a;
    }

    /**
     * Fewest coins needed to make the amount, or -1 if impossible (dynamic programming).
     *
     * How it works:
     *   1. dp[x] = fewest coins to make amount x. dp[0] = 0; everything else starts at 'infinity' (amount + 1).
     *   2. For each amount x from 1 to amount and each coin c <= x: dp[x] = min(dp[x], dp[x - c] + 1), i.e. use coin c on top of the best answer for x - c.
     *   3. If dp[amount] is still 'infinity' the amount cannot be made, so return -1.
     *
     * Example: coins {1, 2, 5}, amount 11  ->  3  (5 + 5 + 1)
     *
     * Complexity: time O(amount * coins), space O(amount)
     */
    public static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);               // "infinity"
        dp[0] = 0;
        for (int x = 1; x <= amount; x++) {
            for (int c : coins) {
                if (c <= x) dp[x] = Math.min(dp[x], dp[x - c] + 1);
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }
}
