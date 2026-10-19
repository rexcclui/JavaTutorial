package prep.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringPracticeTest {
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

    @Test
    void reverseWords() {
        assertThat(StringPractice.reverseWords("  the sky  is blue ")).isEqualTo("blue is sky the");
        assertThat(StringPractice.reverseWords("one")).isEqualTo("one");
    }

    @Test
    void anagram() {
        assertThat(StringPractice.isAnagram("listen", "silent")).isTrue();
        assertThat(StringPractice.isAnagram("abc", "abd")).isFalse();
        assertThat(StringPractice.isAnagram("ab", "abc")).isFalse();
    }

    @Test
    void firstNonRepeated() {
        assertThat(StringPractice.firstNonRepeated("swiss")).isEqualTo(Optional.of('w'));
        assertThat(StringPractice.firstNonRepeated("aabb")).isEmpty();
    }

    // YOUR EDGE CASES HERE (null? unicode? very long input?)
}
