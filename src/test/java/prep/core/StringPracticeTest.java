package prep.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringPracticeTest {
    // PURPOSE: Palindrome check ignoring case, spaces and punctuation, including empty and symbol-only input (two-pointer technique).
    @ParameterizedTest
    @CsvSource({
        "'A man, a plan, a canal: Panama', true",
        "'race a car', false",
        "'', true",
        "'.,', true",
        "'0P', false"
    })
    void palindrome(String input, boolean expected) {
        assertThat(StringPractice.isPalindrome(input)).isEqualTo(expected);
    }

    // PURPOSE: Reverse word order and collapse extra spaces (split/trim handling, a very common warm-up question).
    @Test
    void reverseWords() {
        assertThat(StringPractice.reverseWords("  the sky  is blue ")).isEqualTo("blue is sky the");
        assertThat(StringPractice.reverseWords("one")).isEqualTo("one");
    }

    // PURPOSE: Anagram check: same letters in a different order, and different lengths must fail fast.
    @Test
    void anagram() {
        assertThat(StringPractice.isAnagram("listen", "silent")).isTrue();
        assertThat(StringPractice.isAnagram("abc", "abd")).isFalse();
        assertThat(StringPractice.isAnagram("ab", "abc")).isFalse();
    }

    // PURPOSE: Find the first character that appears exactly once; empty result when every character repeats (counting with insertion order).
    @Test
    void firstNonRepeated() {
        assertThat(StringPractice.firstNonRepeated("swiss")).isEqualTo(Optional.of('w'));
        assertThat(StringPractice.firstNonRepeated("aabb")).isEmpty();
    }

    // YOUR EDGE CASES HERE (null? unicode? very long input?)
}
