package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * QUESTION 12 - string == and compile-time constants
 *
 *     String a = "ab";
 *     String b = "a" + "b";
 *     String c = "a";
 *     String d = c + "b";
 *     System.out.println((a == b) + " " + (a == d));
 *
 * What does it print?
 *
 *   A. true true
 *   B. false false
 *   C. true false
 *   D. false true
 *
 * ANSWER: C
 *
 * WHY:
 *   a == b  is TRUE : "a" + "b" is a compile-time constant, so the compiler folds it into the literal "ab" (same pooled object).
 *   a == d  is FALSE: c is an ordinary variable, so c + "b" is computed when the program runs and creates a NEW String object.
 *   A, B, D each get one of those two comparisons wrong.
 *
 * See it run:  ./run.sh mcq Q12
 */
@SuppressWarnings("StringEquality")
class Q12StringConstantPoolTest {

    // PURPOSE: Demo - print the identity of each string object: same number = same object.
    @Test
    void demo_identityOfEachString() {
        String a = "ab";
        String b = "a" + "b";
        String c = "a";
        String d = c + "b";
        McqSupport.show("a (\"ab\")        identity = %d", System.identityHashCode(a));
        McqSupport.show("b (\"a\" + \"b\")  identity = %d   same object as a? %s   <- folded at compile time", System.identityHashCode(b), a == b);
        McqSupport.show("d (c + \"b\")    identity = %d   same object as a? %s  <- built at run time", System.identityHashCode(d), a == d);
        McqSupport.show("printed line     : %s", (a == b) + " " + (a == d));
        assertThat((a == b) + " " + (a == d)).isEqualTo("true false");
    }

    // PURPOSE: Why a == b - "a" + "b" is a constant expression, folded into "ab" by the compiler.
    @Test
    void why_constantExpressionIsFoldedAtCompileTime() {
        String a = "ab";
        String b = "a" + "b";
        assertThat(a == b).isTrue();
    }

    // PURPOSE: Why a != d - runtime concatenation creates a new object; equals() still says true; intern() returns the pooled one.
    @Test
    void why_runtimeConcatenationCreatesNewObject() {
        String a = "ab";
        String c = "a";
        String d = c + "b";
        McqSupport.show("a == d = %s,  a.equals(d) = %s,  a == d.intern() = %s", a == d, a.equals(d), a == d.intern());
        assertThat(a == d).isFalse();
        assertThat(a.equals(d)).isTrue();
        assertThat(a == d.intern()).isTrue();
    }

    // PURPOSE: Twist - make c a `final` variable initialised with a constant: it becomes a constant variable and c + "b" is folded again.
    @Test
    void twist_finalConstantVariableIsFoldedToo() {
        final String c = "a";
        String d = c + "b";
        McqSupport.show("final String c = \"a\"; d = c + \"b\"; d == \"ab\" -> %s", d == "ab");
        assertThat(d == "ab").isTrue();
    }

    // PURPOSE: Twist - a final variable whose value is only known at runtime is NOT a constant, so no folding.
    @Test
    void twist_finalButNotConstantIsNotFolded() {
        final String c = new String("a");
        String d = c + "b";
        McqSupport.show("final String c = new String(\"a\"); d = c + \"b\"; d == \"ab\" -> %s", d == "ab");
        assertThat(d == "ab").isFalse();
    }
}
