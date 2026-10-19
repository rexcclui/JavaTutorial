package prep.core;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class StringPractice {

    /** Two pointers, O(n) time, O(1) space. */
    public static boolean isPalindrome(String s) {
        int i = 0, j = s.length() - 1;
        while (i < j) {
            while (i < j && !Character.isLetterOrDigit(s.charAt(i))) i++;
            while (i < j && !Character.isLetterOrDigit(s.charAt(j))) j--;
            if (Character.toLowerCase(s.charAt(i++)) != Character.toLowerCase(s.charAt(j--))) {
                return false;
            }
        }
        return true;
    }

    public static String reverseWords(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            if (words[i].isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(words[i]);
        }
        return sb.toString();
    }

    public static boolean isAnagram(String a, String b) {
        if (a.length() != b.length()) return false;
        char[] x = a.toCharArray(), y = b.toCharArray();
        Arrays.sort(x);
        Arrays.sort(y);
        return Arrays.equals(x, y);
        // TWIST: do it in O(n) with an int[65536] counter instead of sorting.
    }

    /** LinkedHashMap keeps insertion order, so the first count==1 entry is the answer. */
    public static Optional<Character> firstNonRepeated(String s) {
        Map<Character, Integer> counts = new LinkedHashMap<>();
        for (char c : s.toCharArray()) counts.merge(c, 1, Integer::sum);
        return counts.entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
