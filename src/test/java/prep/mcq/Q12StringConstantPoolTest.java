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
 */
@SuppressWarnings("StringEquality")
class Q12StringConstantPoolTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'C';

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - the exact snippet from the question, evaluated.
    @Test
    void concept_theQuestion() {
        String a = "ab";
        String b = "a" + "b";
        String c = "a";
        String d = c + "b";
        assertThat((a == b) + " " + (a == d)).isEqualTo("true false");
    }

    // PURPOSE: Experiment for "a == b" - "a" + "b" is a compile-time constant expression, so the compiler folds it into the literal "ab" (same pooled object).
    @Test
    void experiment_constantExpressionIsFoldedAtCompileTime() {
        String a = "ab";
        String b = "a" + "b";
        assertThat(a == b).isTrue();
    }

    // PURPOSE: Experiment for "a == d" - c is a normal variable, so c + "b" is computed when the program RUNS and creates a new String object.
    @Test
    void experiment_runtimeConcatenationCreatesNewObject() {
        String a = "ab";
        String c = "a";
        String d = c + "b";
        assertThat(a == d).isFalse();
        assertThat(a.equals(d)).isTrue();            // same characters, different object
        assertThat(a == d.intern()).isTrue();        // intern() returns the pooled "ab"
    }

    // PURPOSE: Twist - make c a `final` variable initialised with a constant: it becomes a constant variable and c + "b" is folded again.
    @Test
    void twist_finalConstantVariableIsFoldedToo() {
        final String c = "a";                       // final + constant initialiser = constant variable
        String d = c + "b";
        assertThat(d == "ab").isTrue();
    }

    // PURPOSE: Twist - a final variable whose value is only known at runtime is NOT a constant, so no folding.
    @Test
    void twist_finalButNotConstantIsNotFolded() {
        final String c = new String("a");
        String d = c + "b";
        assertThat(d == "ab").isFalse();
    }
}
