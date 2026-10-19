package prep.collections;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionPuzzles {

    /** value -> index map; look for the complement as we go. O(n) time, O(n) space. */
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int j = 0; j < nums.length; j++) {
            Integer i = seen.get(target - nums[j]);
            if (i != null) return new int[] {i, j};
            seen.put(nums[j], j);
        }
        return new int[0];
    }

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

    public static boolean hasDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int n : nums) if (!seen.add(n)) return true;
        return false;
    }

    /** Only start counting from the beginning of a run (n-1 absent) -> each element visited O(1) times. */
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
