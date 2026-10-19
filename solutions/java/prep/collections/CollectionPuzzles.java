package prep.collections;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Problems where the main work is choosing the right collection (map, set, sorted order).
 */
public class CollectionPuzzles {

    /**
     * Finds two different positions whose values add up to the target.
     *
     * How it works:
     *   1. Keep a map from 'value seen so far' to its index.
     *   2. For each number x, the partner we need is target - x. If that partner is already in the map, we found the pair.
     *   3. Otherwise remember x and its index and continue.
     *   4. Return an empty array if no pair exists.
     *
     * Example: {2, 7, 11, 15}, target 9  ->  [0, 1]
     *
     * Complexity: time O(n), space O(n)
     */
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int j = 0; j < nums.length; j++) {
            Integer i = seen.get(target - nums[j]);
            if (i != null) return new int[] {i, j};
            seen.put(nums[j], j);
        }
        return new int[0];
    }

    /**
     * The k most frequent words, ignoring case and punctuation; ties are broken alphabetically.
     *
     * How it works:
     *   1. Lower-case the text and split on anything that is not a letter.
     *   2. Drop empty pieces, then count each word with groupingBy + counting.
     *   3. Sort the entries by count (highest first), then by word (A to Z).
     *   4. Keep the first k words.
     *
     * Example: "the cat and the hat and THE bat", k=2  ->  [the, and]
     *
     * Complexity: time O(n + m log m) for m distinct words, space O(m)
     */
    public static List<String> topKWords(String text, int k) {
        Map<String, Long> counts = Arrays.stream(text.toLowerCase().split("[^a-z]+"))
                .filter(w -> !w.isEmpty())
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(k)
                .map(Map.Entry::getKey)
                .toList();
        // TWIST: use a PriorityQueue of size k for O(n log k) instead of sorting everything.
    }

    /**
     * True if any value appears more than once.
     *
     * How it works:
     *   1. Add each number to a HashSet.
     *   2. Set.add returns false when the value was already there, so return true at that moment.
     *
     * Example: {1, 2, 3, 1}  ->  true
     *
     * Complexity: time O(n), space O(n)
     */
    public static boolean hasDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int n : nums) if (!seen.add(n)) return true;
        return false;
    }

    /**
     * Length of the longest run of consecutive integers (like 1,2,3,4), no matter the order in the array.
     *
     * How it works:
     *   1. Put all numbers in a set (this also removes duplicates).
     *   2. Only start counting at the START of a run: a number n where n - 1 is NOT in the set.
     *   3. From that start keep checking n+1, n+2, ... while they are in the set, and count the length.
     *   4. Remember the best length. Each number is visited only a constant number of times.
     *
     * Example: {100, 4, 200, 1, 3, 2}  ->  4  (the run 1,2,3,4)
     *
     * Complexity: time O(n), space O(n)
     */
    public static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);
        int best = 0;
        for (int n : set) {
            if (set.contains(n - 1)) continue;
            int len = 1;
            while (set.contains(n + len)) len++;
            best = Math.max(best, len);
        }
        return best;
    }
}
