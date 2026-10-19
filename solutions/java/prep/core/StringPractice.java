package prep.core;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Classic string interview questions: palindrome, reversing words, anagram and first non-repeated character.
 */
public class StringPractice {

    /**
     * True if the text reads the same forwards and backwards, ignoring case and any character that is not a letter or digit.
     *
     * How it works:
     *   1. Put one pointer i at the start and one pointer j at the end.
     *   2. Move i right and j left, skipping characters that are not letters or digits.
     *   3. Compare the two characters in lower case; if they differ, return false.
     *   4. Move both pointers inward and repeat until they meet; then it is a palindrome.
     *
     * Example: "A man, a plan, a canal: Panama"  ->  true
     *
     * Complexity: time O(n), space O(1)
     */
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

    /**
     * Reverses the order of the words and collapses repeated spaces.
     *
     * How it works:
     *   1. trim() removes leading and trailing spaces.
     *   2. split("\\s+") cuts the text into words on one or more whitespace characters.
     *   3. Walk the words from last to first and append each to a StringBuilder, with a single space between words.
     *
     * Example: "  the sky  is blue "  ->  "blue is sky the"
     *
     * Complexity: time O(n), space O(n)
     */
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

    /**
     * True if b is a rearrangement of the letters of a (case-sensitive).
     *
     * How it works:
     *   1. If the lengths differ they cannot be anagrams, so return false immediately.
     *   2. Sort the characters of both strings.
     *   3. Two anagrams have identical sorted character arrays, so compare them.
     *
     * Example: "listen", "silent"  ->  true
     *
     * Complexity: time O(n log n) because of the sort, space O(n)
     */
    public static boolean isAnagram(String a, String b) {
        if (a.length() != b.length()) return false;
        char[] x = a.toCharArray(), y = b.toCharArray();
        Arrays.sort(x);
        Arrays.sort(y);
        return Arrays.equals(x, y);
        // TWIST: do it in O(n) with an int[65536] counter instead of sorting.
    }

    /**
     * Returns the first character that appears exactly once, or an empty Optional if every character repeats.
     *
     * How it works:
     *   1. Count each character in a LinkedHashMap. It remembers insertion order, so characters stay in the order first seen.
     *   2. Stream over the entries, keep those with count 1, and take the first one.
     *
     * Example: "swiss"  ->  Optional['w']
     *
     * Complexity: time O(n), space O(k) where k is the number of distinct characters
     */
    public static Optional<Character> firstNonRepeated(String s) {
        Map<Character, Integer> counts = new LinkedHashMap<>();
        for (char c : s.toCharArray()) counts.merge(c, 1, Integer::sum);
        return counts.entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
