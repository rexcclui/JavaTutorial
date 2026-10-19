package prep.algorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArrayTricks {

    /** Sliding window with last-seen index map. O(n). */
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

    public static int maxSubarray(int[] a) {
        int best = a[0], cur = a[0];
        for (int i = 1; i < a.length; i++) {
            cur = Math.max(a[i], cur + a[i]);
            best = Math.max(best, cur);
        }
        return best;
    }

    public static long fib(int n) {
        long a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            long t = a + b;
            a = b;
            b = t;
        }
        return a;
    }

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
