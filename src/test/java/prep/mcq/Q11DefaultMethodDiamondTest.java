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
 *
 * ANSWER: B
 *
 * WHY:
 *   A. Wrong - the compiler refuses to guess; the order of the interfaces is irrelevant.
 *   B. Correct - "class C inherits unrelated defaults for m() from types A and B". Override m() to resolve.
 *   C. Wrong - the problem is caught at compile time, so there is no runtime state to fail in.
 *   D. Wrong - same as A.
 *
 * See it run:  ./run.sh mcq Q11
 */
class Q11DefaultMethodDiamondTest {

    private static String source(String implementsClause, String body) {
        return """
                public class Snippet {
                    interface A { default String m() { return "A"; } }
                    interface B { default String m() { return "B"; } }
                    static class C implements %s { %s }
                }
                """.formatted(implementsClause, body);
    }

    // PURPOSE: Demo - the fix: override m() and choose (or combine) the inherited defaults with X.super.m().
    @Test
    void demo_resolveByOverriding() {
        interface A { default String m() { return "A"; } }
        interface B { default String m() { return "B"; } }
        class C implements A, B {
            @Override public String m() { return A.super.m() + B.super.m(); }
        }
        McqSupport.show("new C().m() = %s   (A.super.m() + B.super.m())", new C().m());
        assertThat(new C().m()).isEqualTo("AB");
    }

    // PURPOSE: Options A and D are WRONG - run the real compiler on both interface orders: both are rejected.
    @Test
    void optionAandD_wrong_compilerRejectsBothOrders() {
        List<String> errorsAB = McqSupport.compileErrors("Snippet", source("A, B", ""));
        List<String> errorsBA = McqSupport.compileErrors("Snippet", source("B, A", ""));
        McqSupport.show("implements A, B -> %s", errorsAB);
        McqSupport.show("implements B, A -> %s", errorsBA);
        assertThat(errorsAB).isNotEmpty();
        assertThat(errorsBA).isNotEmpty();
        assertThat(String.join(" ", errorsAB)).contains("unrelated defaults");
    }

    // PURPOSE: Option B is CORRECT - overriding m() makes the very same code compile.
    @Test
    void optionB_correct_overrideCompiles() {
        String fixed = source("A, B", "public String m() { return A.super.m(); }");
        var errors = McqSupport.compileErrors("Snippet", fixed);
        McqSupport.show("with `public String m() { return A.super.m(); }` -> errors: %s", errors);
        assertThat(errors).isEmpty();
    }

    // PURPOSE: Option C is WRONG - the error exists at COMPILE time (exactly one compile error), so no class file is produced to fail later.
    @Test
    void optionC_wrong_failsAtCompileTimeNotRuntime() {
        assertThat(McqSupport.compileErrors("Snippet", source("A, B", ""))).hasSize(1);
        McqSupport.show("exactly one compile error, reported before anything can run");
    }

    // PURPOSE: Bonus rule - if one interface extends the other and overrides m(), the more specific default wins and there is no conflict.
    @Test
    void bonus_moreSpecificInterfaceWins() {
        interface Base { default String m() { return "Base"; } }
        interface Special extends Base { @Override default String m() { return "Special"; } }
        class C implements Base, Special { }
        McqSupport.show("class C implements Base, Special -> m() = %s", new C().m());
        assertThat(new C().m()).isEqualTo("Special");
    }
}
