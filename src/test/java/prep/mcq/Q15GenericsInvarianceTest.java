package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 15 - generics are invariant
 *
 *     List<Object> a = new ArrayList<String>();
 *
 * What happens?
 *
 *   A. It compiles and runs fine, because String is a subtype of Object.
 *   B. Compile error: List<String> is not a List<Object> (generics are invariant).
 *   C. It compiles but throws ClassCastException at runtime.
 *   D. It compiles with only an unchecked warning.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class Q15GenericsInvarianceTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'B';

    private static String wrap(String statement) {
        return "import java.util.*; public class Snippet { void m() { " + statement + " } }";
    }

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - the real compiler's verdict on the statement from the question, and on the legal alternatives.
    @Test
    void concept_compilerVerdicts() {
        assertThat(McqSupport.compileErrors("Snippet", wrap("List<Object> a = new ArrayList<String>();"))).isNotEmpty();
        assertThat(McqSupport.compileErrors("Snippet", wrap("List<? extends Object> a = new ArrayList<String>();"))).isEmpty();
        assertThat(McqSupport.compileErrors("Snippet", wrap("List<Object> a = new ArrayList<Object>();"))).isEmpty();
    }

    // PURPOSE: Experiment for options A and D - if it compiled (with or without a warning) there would be no error message. There is an error.
    @Test
    void experimentAandD_isAnErrorNotAWarning() {
        var errors = McqSupport.compileErrors("Snippet", wrap("List<Object> a = new ArrayList<String>();"));
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("incompatible types");
        // Verdict: A and D are false.
    }

    // PURPOSE: Experiment for option B - WHY it is forbidden: if it were allowed, you could put an Integer into what is really a list of Strings.
    @Test
    void experimentB_whyInvarianceExists() {
        List<String> strings = new ArrayList<>();
        List<Object> objects = (List) strings;           // force it with a raw cast - this is what the compiler prevents
        objects.add(42);                                 // compiles, "works"... heap pollution
        assertThatThrownBy(() -> { String s = strings.get(0); }).isInstanceOf(ClassCastException.class);
        // Verdict: B is true - the compile error protects you from this runtime failure.
    }

    // PURPOSE: Experiment for option C - the ClassCastException in experimentB only appears when you bypass generics with raw types; the original statement never gets that far.
    @Test
    void experimentC_castExceptionNeedsRawTypes() {
        assertThat(McqSupport.compileErrors("Snippet", wrap("List<Object> a = new ArrayList<String>();"))).isNotEmpty();
        // Verdict: C is false: a statement that does not compile cannot fail at runtime.
    }

    // PURPOSE: Contrast - ARRAYS are covariant (String[] is an Object[]), which is allowed but fails at runtime with ArrayStoreException.
    @Test
    void contrast_arraysAreCovariant() {
        Object[] objects = new String[1];
        assertThatThrownBy(() -> objects[0] = 1).isInstanceOf(ArrayStoreException.class);
    }
}
