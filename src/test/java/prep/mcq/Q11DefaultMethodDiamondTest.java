package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 11 - default methods and the diamond problem
 *
 * Interfaces A and B each declare a default method m(). A class C implements both A and B without declaring m(). What happens?
 *
 *   A. It compiles; the default from the first-listed interface (A) is used.
 *   B. It does not compile, unless C overrides m() itself (it can delegate with A.super.m() or B.super.m()).
 *   C. It compiles but throws an AbstractMethodError when m() is called.
 *   D. It compiles; the default from the last-listed interface (B) is used.
 */
class Q11DefaultMethodDiamondTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'B';

    private static String source(String implementsClause, String body) {
        return """
                public class Snippet {
                    interface A { default String m() { return "A"; } }
                    interface B { default String m() { return "B"; } }
                    static class C implements %s { %s }
                }
                """.formatted(implementsClause, body);
    }

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - the fix: override m() and choose (or combine) the inherited defaults with X.super.m().
    @Test
    void concept_resolveByOverriding() {
        interface A { default String m() { return "A"; } }
        interface B { default String m() { return "B"; } }
        class C implements A, B {
            @Override public String m() { return A.super.m() + B.super.m(); }
        }
        assertThat(new C().m()).isEqualTo("AB");
    }

    // PURPOSE: Experiment for options A and D - if either rule existed, the code would compile. We run the real compiler on it.
    @Test
    void experimentAandD_compilerRejectsBothOrders() {
        List<String> errorsAB = McqSupport.compileErrors("Snippet", source("A, B", ""));
        List<String> errorsBA = McqSupport.compileErrors("Snippet", source("B, A", ""));
        assertThat(errorsAB).isNotEmpty();
        assertThat(errorsBA).isNotEmpty();                      // swapping the order does not help
        assertThat(String.join(" ", errorsAB)).contains("unrelated defaults");
        // Verdict: A and D are false. The order of the interfaces is irrelevant; the compiler refuses to guess.
    }

    // PURPOSE: Experiment for option B - overriding m() makes the very same code compile.
    @Test
    void experimentB_overrideCompiles() {
        String fixed = source("A, B", "public String m() { return A.super.m(); }");
        assertThat(McqSupport.compileErrors("Snippet", fixed)).isEmpty();
        // Verdict: B is true.
    }

    // PURPOSE: Experiment for option C - the problem is reported at COMPILE time, so there is never a runtime state to fail in.
    @Test
    void experimentC_failsAtCompileTimeNotRuntime() {
        assertThat(McqSupport.compileErrors("Snippet", source("A, B", ""))).hasSize(1);
        // Verdict: C is false.
    }

    // PURPOSE: Bonus rule - if one interface extends the other and overrides m(), the more specific default wins and there is no conflict.
    @Test
    void bonus_moreSpecificInterfaceWins() {
        interface Base { default String m() { return "Base"; } }
        interface Special extends Base { @Override default String m() { return "Special"; } }
        class C implements Base, Special { }
        assertThat(new C().m()).isEqualTo("Special");
    }
}
