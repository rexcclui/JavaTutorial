package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 24 - checked vs unchecked exceptions
 *
 * Which method body compiles when the method declares NO throws clause?
 *
 *   A. void m() { throw new java.io.IOException("x"); }
 *   B. void m() { throw new Exception("x"); }
 *   C. void m() { throw new IllegalStateException("x"); }
 *   D. void m() { try { } catch (java.io.IOException e) { } }
 *
 * ANSWER: C
 *
 * WHY:
 *   A, B. Wrong - IOException and Exception are CHECKED: you must catch them or declare them with `throws`.
 *   C. Correct - IllegalStateException is a RuntimeException (unchecked); no declaration needed.
 *   D. Wrong - catching a CHECKED exception that the try block can never throw is itself a compile error
 *      ("exception IOException is never thrown in body of corresponding try statement").
 *
 * Rule of thumb: checked = Exception and its subclasses EXCEPT RuntimeException (and Error is unchecked too).
 *
 * See it run:  ./run.sh mcq Q24
 */
class Q24CheckedExceptionsTest {

    private static List<String> compile(String methodBody) {
        return McqSupport.compileErrors("Snippet", "public class Snippet { " + methodBody + " }");
    }

    // PURPOSE: Demo - run the real compiler on all four candidates and print its verdict for each.
    @Test
    void demo_compilerVerdictOnEachOption() {
        String[] bodies = {
            "void m() { throw new java.io.IOException(\"x\"); }",
            "void m() { throw new Exception(\"x\"); }",
            "void m() { throw new IllegalStateException(\"x\"); }",
            "void m() { try { } catch (java.io.IOException e) { } }"};
        for (int i = 0; i < bodies.length; i++) {
            var errors = compile(bodies[i]);
            McqSupport.show("%c. %-62s -> %s", 'A' + i, bodies[i], errors.isEmpty() ? "compiles" : "ERROR: " + errors.get(0));
        }
        assertThat(compile(bodies[0])).isNotEmpty();
        assertThat(compile(bodies[1])).isNotEmpty();
        assertThat(compile(bodies[2])).isEmpty();
        assertThat(compile(bodies[3])).isNotEmpty();
    }

    // PURPOSE: Option A is WRONG - IOException is checked: "unreported exception".
    @Test
    void optionA_wrong_ioExceptionIsChecked() {
        assertThat(String.join(" ", compile("void m() { throw new java.io.IOException(\"x\"); }"))).contains("unreported exception");
    }

    // PURPOSE: Option B is WRONG - plain Exception is checked too.
    @Test
    void optionB_wrong_exceptionIsChecked() {
        assertThat(String.join(" ", compile("void m() { throw new Exception(\"x\"); }"))).contains("unreported exception");
    }

    // PURPOSE: Option C is CORRECT - a RuntimeException subclass needs no declaration.
    @Test
    void optionC_correct_runtimeExceptionIsUnchecked() {
        assertThat(compile("void m() { throw new IllegalStateException(\"x\"); }")).isEmpty();
        assertThat(RuntimeException.class.isAssignableFrom(IllegalStateException.class)).isTrue();
        assertThat(RuntimeException.class.isAssignableFrom(java.io.IOException.class)).isFalse();
    }

    // PURPOSE: Option D is WRONG - catching a checked exception the try block cannot throw is an error (but catching Exception is allowed).
    @Test
    void optionD_wrong_unreachableCatchOfCheckedException() {
        assertThat(String.join(" ", compile("void m() { try { } catch (java.io.IOException e) { } }"))).contains("is never thrown");
        assertThat(compile("void m() { try { } catch (Exception e) { } }")).isEmpty();   // Exception is allowed: it also covers unchecked ones
    }

    // PURPOSE: Fix - declare the checked exception with `throws` and the very same code compiles.
    @Test
    void fix_declareWithThrows() {
        assertThat(compile("void m() throws java.io.IOException { throw new java.io.IOException(\"x\"); }")).isEmpty();
    }
}
