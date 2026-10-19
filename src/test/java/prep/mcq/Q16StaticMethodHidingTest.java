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
 */
@SuppressWarnings("static")
class Q16StaticMethodHidingTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'D';

    static class A {
        static String who() { return "A"; }
        String name() { return "A"; }
    }

    static class B extends A {
        static String who() { return "B"; }          // HIDES A.who(), does not override it
        @Override String name() { return "B"; }     // really overrides
    }

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - an instance method is chosen by the REAL object type (polymorphism); a static method by the DECLARED type of the reference.
    @Test
    void concept_staticByDeclaredTypeInstanceByRealType() {
        A a = new B();
        assertThat(a.name()).isEqualTo("B");         // overriding: runtime type B wins
        assertThat(a.who()).isEqualTo("A");          // hiding: declared type A wins
    }

    // PURPOSE: Experiment for option A - does the reference variable's runtime type (B) decide? Only if we change the DECLARED type.
    @Test
    void experimentA_declaredTypeDecides() {
        A a = new B();
        assertThat(((B) a).who()).isEqualTo("B");    // cast changes the declared type
        assertThat(B.who()).isEqualTo("B");
        assertThat(A.who()).isEqualTo("A");
        // Verdict: A is false.
    }

    // PURPOSE: Experiment for option B - it does compile: Java lets you call a static method through an instance reference (the compiler only warns). We compile the exact snippet.
    @Test
    void experimentB_compiles() {
        String source = """
                public class Snippet {
                    static class A { static String who() { return "A"; } }
                    static class B extends A { static String who() { return "B"; } }
                    static String run() { A a = new B(); return a.who(); }
                }
                """;
        assertThat(McqSupport.compileErrors("Snippet", source)).isEmpty();
        // Verdict: B is false. Best practice: always write A.who(), never a.who().
    }

    // PURPOSE: Experiment for option C - a null reference does not even matter for a static call, because no object is needed.
    @Test
    void experimentC_noException_evenWithNullReference() {
        A nothing = null;
        assertThat(nothing.who()).isEqualTo("A");    // no NullPointerException: resolved at compile time from the declared type A
        // Verdict: C is false.
    }
}
