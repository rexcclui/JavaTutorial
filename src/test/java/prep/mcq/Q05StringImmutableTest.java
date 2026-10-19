package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * QUESTION 5 - Strings are immutable
 *
 * What does this code print?
 *
 *     String s = "a";
 *     s.concat("b");
 *     System.out.println(s);
 *
 *   A. a
 *   B. ab
 *   C. b
 *   D. It does not compile.
 */
class Q05StringImmutableTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'A';

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - String methods never change the string; they RETURN a new one. Ignoring the return value loses the result.
    @Test
    void concept_resultMustBeAssigned() {
        String s = "a";
        s.concat("b");                       // result thrown away
        assertThat(s).isEqualTo("a");

        s = s.concat("b");                   // assign the returned new string
        assertThat(s).isEqualTo("ab");
    }

    // PURPOSE: Experiment for option B - would the original string become "ab"? Show the original is untouched and the new one is a different object.
    @Test
    void experimentB_originalUnchanged() {
        String s = "a";
        String result = s.concat("b");
        assertThat(s).isEqualTo("a");
        assertThat(result).isEqualTo("ab");
        assertThat(result).isNotSameAs(s);
        // Verdict: B would be the output of `System.out.println(s.concat("b"))` or `s = s.concat("b")`, not of the given code.
    }

    // PURPOSE: Experiment for option C - concat appends at the END of the string; nothing produces just "b" from "a".concat("b") on s.
    @Test
    void experimentC_concatAppends() {
        assertThat("a".concat("b")).isEqualTo("ab");
        assertThat("a".concat("b")).isNotEqualTo("b");
    }

    // PURPOSE: Experiment for option D - the code compiles. Calling a method and ignoring its return value is legal Java (we compile the exact snippet).
    @Test
    void experimentD_snippetCompiles() {
        String source = """
                public class Snippet {
                    public static void main(String[] args) {
                        String s = "a";
                        s.concat("b");
                        System.out.println(s);
                    }
                }
                """;
        assertThat(McqSupport.compileErrors("Snippet", source)).isEmpty();
    }

    // PURPOSE: Contrast - StringBuilder IS mutable, so append changes the same object.
    @Test
    void contrast_stringBuilderMutates() {
        StringBuilder sb = new StringBuilder("a");
        sb.append("b");
        assertThat(sb.toString()).isEqualTo("ab");
    }
}
