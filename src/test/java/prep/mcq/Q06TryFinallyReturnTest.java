package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * QUESTION 6 - return value and finally
 *
 * What does f() return?
 *
 *     static int f() {
 *         int x = 1;
 *         try {
 *             return x;
 *         } finally {
 *             x = 2;
 *         }
 *     }
 *
 *   A. 2
 *   B. It does not compile.
 *   C. 1
 *   D. It throws an exception.
 */
class Q06TryFinallyReturnTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'C';

    static int f() {
        int x = 1;
        try {
            return x;
        } finally {
            x = 2;
        }
    }

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - `return x` copies the value of x first; the finally block runs afterwards but cannot change that saved value.
    @Test
    void concept_returnValueIsSavedBeforeFinally() {
        assertThat(f()).isEqualTo(1);
    }

    // PURPOSE: Experiment for option A - 2 is what you get only if finally itself contains a `return 2`, which overrides the try's return.
    @Test
    void experimentA_twoOnlyWhenFinallyReturns() {
        assertThat(g()).isEqualTo(2);
        // Verdict: A is the answer for g() (return inside finally), not for f() (assignment inside finally).
    }

    @SuppressWarnings("finally")
    private static int g() {
        try {
            return 1;
        } finally {
            return 2;
        }
    }

    // PURPOSE: Experiment for option B - the snippet compiles. We compile the exact source.
    @Test
    void experimentB_compiles() {
        String source = """
                public class Snippet {
                    static int f() {
                        int x = 1;
                        try { return x; } finally { x = 2; }
                    }
                }
                """;
        assertThat(McqSupport.compileErrors("Snippet", source)).isEmpty();
    }

    // PURPOSE: Experiment for option D - nothing in f() throws, so no exception. (A return in finally can even swallow an exception.)
    @Test
    void experimentD_noException() {
        assertThat(f()).isEqualTo(1);
        assertThat(swallow()).isEqualTo("swallowed");   // bonus: return in finally hides the exception
    }

    @SuppressWarnings("finally")
    private static String swallow() {
        try {
            throw new IllegalStateException("lost");
        } finally {
            return "swallowed";
        }
    }

    // PURPOSE: Contrast - with an object (reference) the finally block CAN change the object's contents, because the returned reference is the same object.
    @Test
    void contrast_mutableObjectIsChanged() {
        assertThat(h().toString()).isEqualTo("ab");
    }

    private static StringBuilder h() {
        StringBuilder sb = new StringBuilder("a");
        try {
            return sb;
        } finally {
            sb.append("b");
        }
    }
}
