package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * QUESTION 16 - static methods are not overridden
 *
 *     class A { static String who() { return "A"; } }
 *     class B extends A { static String who() { return "B"; } }
 *
 *     A a = new B();
 *     System.out.println(a.who());
 *
 * What is printed?
 *
 *   A. B
 *   B. It does not compile: static methods cannot be called on an instance.
 *   C. It throws an exception.
 *   D. A
 *
 * ANSWER: D
 *
 * WHY:
 *   A. Wrong - static methods are chosen by the DECLARED type of the reference (A), not the real object (B).
 *   B. Wrong - it compiles (only a style warning); better write A.who().
 *   C. Wrong - no exception, not even if the reference is null, because no object is needed for a static call.
 *   D. Correct - B.who() HIDES A.who(); it does not override it. Instance methods are the ones with runtime polymorphism.
 *
 * See it run:  ./run.sh mcq Q16
 */
@SuppressWarnings("static")
class Q16StaticMethodHidingTest {

    static class A {
        static String who() { return "A"; }
        String name() { return "A"; }
    }

    static class B extends A {
        static String who() { return "B"; }          // HIDES A.who(), does not override it
        @Override String name() { return "B"; }     // really overrides
    }

    // PURPOSE: Demo - same object (a B), two kinds of method: instance method follows the real type, static method follows the declared type.
    @Test
    void demo_staticByDeclaredTypeInstanceByRealType() {
        A a = new B();
        McqSupport.show("A a = new B();");
        McqSupport.show("a.name()  = %s   (instance method: real type B wins - overriding)", a.name());
        McqSupport.show("a.who()   = %s   (static method: declared type A wins - hiding)", a.who());
        assertThat(a.name()).isEqualTo("B");
        assertThat(a.who()).isEqualTo("A");
    }

    // PURPOSE: Option A is WRONG - only changing the DECLARED type changes the answer.
    @Test
    void optionA_wrong_declaredTypeDecides() {
        A a = new B();
        McqSupport.show("((B) a).who() = %s,  B.who() = %s,  A.who() = %s", ((B) a).who(), B.who(), A.who());
        assertThat(((B) a).who()).isEqualTo("B");
        assertThat(A.who()).isEqualTo("A");
    }

    // PURPOSE: Option B is WRONG - the exact snippet compiles (checked with the real compiler).
    @Test
    void optionB_wrong_compiles() {
        String source = """
                public class Snippet {
                    static class A { static String who() { return "A"; } }
                    static class B extends A { static String who() { return "B"; } }
                    static String run() { A a = new B(); return a.who(); }
                }
                """;
        var errors = McqSupport.compileErrors("Snippet", source);
        McqSupport.show("compiler errors: %s", errors.isEmpty() ? "none - it compiles" : errors);
        assertThat(errors).isEmpty();
    }

    // PURPOSE: Option C is WRONG - a null reference does not matter for a static call, because the method is resolved at compile time.
    @Test
    void optionC_wrong_noExceptionEvenWithNullReference() {
        A nothing = null;
        McqSupport.show("A nothing = null;  nothing.who() = %s   (no NullPointerException)", nothing.who());
        assertThat(nothing.who()).isEqualTo("A");
    }
}
