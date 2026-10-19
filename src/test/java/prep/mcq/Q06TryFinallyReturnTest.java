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
 *
 * ANSWER: C
 *
 * WHY:
 *   A. Wrong - 2 is returned only if finally itself contains `return 2`. Here finally only assigns x.
 *   B. Wrong - it compiles.
 *   C. Correct - `return x` copies the value (1) to be returned BEFORE finally runs; finally changes x afterwards, too late.
 *   D. Wrong - nothing throws.
 *
 * See it run:  ./run.sh mcq Q06
 */
class Q06TryFinallyReturnTest {

    static int f() {
        int x = 1;
        try {
            McqSupport.show("  try:     return x   -> value 1 is saved for returning");
            return x;
        } finally {
            x = 2;
            McqSupport.show("  finally: x = 2      -> x changes, but the saved return value stays 1");
        }
    }

    // PURPOSE: Demo - step-by-step trace of f(): the return value is saved before finally runs.
    @Test
    void demo_returnValueIsSavedBeforeFinally() {
        int result = f();
        McqSupport.show("f() returned %d", result);
        assertThat(result).isEqualTo(1);
    }

    // PURPOSE: Option A is WRONG for f() - 2 comes only from a `return` inside finally (shown with g()).
    @Test
    void optionA_wrong_twoOnlyWhenFinallyReturns() {
        McqSupport.show("g() { try { return 1; } finally { return 2; } }  ->  %d", g());
        assertThat(g()).isEqualTo(2);
    }

    @SuppressWarnings("finally")
    private static int g() {
        try {
            return 1;
        } finally {
            return 2;
        }
    }

    // PURPOSE: Option B is WRONG - the snippet compiles (checked with the real compiler).
    @Test
    void optionB_wrong_compiles() {
        String source = """
                public class Snippet {
                    static int f() {
                        int x = 1;
                        try { return x; } finally { x = 2; }
                    }
                }
                """;
        var errors = McqSupport.compileErrors("Snippet", source);
        McqSupport.show("compiler errors: %s", errors.isEmpty() ? "none - it compiles" : errors);
        assertThat(errors).isEmpty();
    }

    // PURPOSE: Option C is CORRECT - and a `return` in finally can even swallow an exception (bonus).
    @Test
    void optionC_correct_andBonusSwallowedException() {
        McqSupport.show("f() = %d", f());
        McqSupport.show("swallow() = \"%s\"   (the IllegalStateException thrown in try vanished)", swallow());
        assertThat(f()).isEqualTo(1);
        assertThat(swallow()).isEqualTo("swallowed");
    }

    @SuppressWarnings("finally")
    private static String swallow() {
        try {
            throw new IllegalStateException("lost");
        } finally {
            return "swallowed";
        }
    }

    // PURPOSE: Option D is WRONG - nothing in f() throws.
    @Test
    void optionD_wrong_noException() {
        McqSupport.attempt("f()", Q06TryFinallyReturnTest::f);
    }

    // PURPOSE: Contrast - with a mutable OBJECT the finally block CAN change what the caller sees, because the returned reference points to the same object.
    @Test
    void contrast_mutableObjectIsChanged() {
        StringBuilder result = h();
        McqSupport.show("h() returned \"%s\"   (finally appended \"b\" to the same object)", result);
        assertThat(result.toString()).isEqualTo("ab");
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
