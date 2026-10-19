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
 *
 * ANSWER: A
 *
 * WHY:
 *   A. Correct - concat() does not change s; it RETURNS a new String, which the code throws away.
 *   B. Wrong - that would be printed by System.out.println(s.concat("b")) or after s = s.concat("b").
 *   C. Wrong - concat appends at the end; nothing produces just "b".
 *   D. Wrong - it compiles; ignoring a method's return value is legal Java.
 *
 * See it run:  ./run.sh mcq Q05
 */
class Q05StringImmutableTest {

    // PURPOSE: Demo - String methods never change the string; they return a new one. Ignoring the return value loses the result.
    @Test
    void demo_resultMustBeAssigned() {
        String s = "a";
        s.concat("b");                       // result thrown away
        McqSupport.show("after s.concat(\"b\")        : s = \"%s\"   (unchanged)", s);
        assertThat(s).isEqualTo("a");

        s = s.concat("b");                   // assign the returned new string
        McqSupport.show("after s = s.concat(\"b\")    : s = \"%s\"", s);
        assertThat(s).isEqualTo("ab");
    }

    // PURPOSE: Option A is CORRECT - the original stays "a"; the result "ab" is a different object that nobody kept.
    @Test
    void optionA_correct_originalUnchanged() {
        String s = "a";
        String result = s.concat("b");
        McqSupport.show("s      = \"%s\"", s);
        McqSupport.show("result = \"%s\"   same object as s? %s", result, result == s);
        assertThat(s).isEqualTo("a");
        assertThat(result).isNotSameAs(s);
    }

    // PURPOSE: Option B is WRONG - "ab" appears only if you use the returned value.
    @Test
    void optionB_wrong_onlyIfYouUseTheReturnValue() {
        String s = "a";
        McqSupport.show("println(s.concat(\"b\")) would print: %s", s.concat("b"));
        assertThat(s.concat("b")).isEqualTo("ab");
        assertThat(s).isEqualTo("a");
    }

    // PURPOSE: Option C is WRONG - concat appends to the END; it never yields just "b".
    @Test
    void optionC_wrong_concatAppends() {
        McqSupport.show("\"a\".concat(\"b\") = \"%s\"", "a".concat("b"));
        assertThat("a".concat("b")).isEqualTo("ab").isNotEqualTo("b");
    }

    // PURPOSE: Option D is WRONG - the exact snippet compiles (checked with the real compiler).
    @Test
    void optionD_wrong_snippetCompiles() {
        String source = """
                public class Snippet {
                    public static void main(String[] args) {
                        String s = "a";
                        s.concat("b");
                        System.out.println(s);
                    }
                }
                """;
        var errors = McqSupport.compileErrors("Snippet", source);
        McqSupport.show("compiler errors: %s", errors.isEmpty() ? "none - it compiles" : errors);
        assertThat(errors).isEmpty();
    }

    // PURPOSE: Contrast - StringBuilder IS mutable: append changes the same object.
    @Test
    void contrast_stringBuilderMutates() {
        StringBuilder sb = new StringBuilder("a");
        sb.append("b");
        McqSupport.show("StringBuilder after append(\"b\") -> %s  (same object changed)", sb);
        assertThat(sb.toString()).isEqualTo("ab");
    }
}
