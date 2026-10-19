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
 *
 * ANSWER: B
 *
 * WHY:
 *   A, D. Wrong - it is a hard compile ERROR ("incompatible types"), not a warning.
 *   B. Correct - if it were allowed you could add an Integer to what is really a list of Strings.
 *   C. Wrong - a statement that does not compile cannot fail at runtime. (ClassCastException only appears if you sneak past generics with raw types.)
 *
 * See it run:  ./run.sh mcq Q15
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class Q15GenericsInvarianceTest {

    private static String wrap(String statement) {
        return "import java.util.*; public class Snippet { void m() { " + statement + " } }";
    }

    // PURPOSE: Demo - the real compiler's verdict on the statement from the question, and on the legal alternatives.
    @Test
    void demo_compilerVerdicts() {
        String[] statements = {
            "List<Object> a = new ArrayList<String>();",
            "List<? extends Object> a = new ArrayList<String>();",
            "List<Object> a = new ArrayList<Object>();"};
        for (String statement : statements) {
            var errors = McqSupport.compileErrors("Snippet", wrap(statement));
            McqSupport.show("%-55s -> %s", statement, errors.isEmpty() ? "compiles" : "ERROR: " + errors.get(0).lines().findFirst().orElse(""));
        }
        assertThat(McqSupport.compileErrors("Snippet", wrap(statements[0]))).isNotEmpty();
        assertThat(McqSupport.compileErrors("Snippet", wrap(statements[1]))).isEmpty();
        assertThat(McqSupport.compileErrors("Snippet", wrap(statements[2]))).isEmpty();
    }

    // PURPOSE: Options A and D are WRONG - it is an error, not a warning: exactly one "incompatible types" compile error.
    @Test
    void optionAandD_wrong_isAnErrorNotAWarning() {
        var errors = McqSupport.compileErrors("Snippet", wrap("List<Object> a = new ArrayList<String>();"));
        McqSupport.show("compile errors: %s", errors);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("incompatible types");
    }

    // PURPOSE: Option B is CORRECT - WHY invariance exists: force it with a raw cast and an Integer lands in a "list of Strings".
    @Test
    void optionB_correct_whyInvarianceExists() {
        List<String> strings = new ArrayList<>();
        List<Object> objects = (List) strings;           // the cast the compiler prevents in normal code
        objects.add(42);
        McqSupport.show("added 42 through the List<Object> alias; the 'List<String>' now contains: %s", strings);
        McqSupport.attempt("String s = strings.get(0)", () -> { String s = strings.get(0); });
        assertThatThrownBy(() -> { String s = strings.get(0); }).isInstanceOf(ClassCastException.class);
    }

    // PURPOSE: Option C is WRONG - the ClassCastException needs raw types; the original statement never gets as far as running.
    @Test
    void optionC_wrong_castExceptionNeedsRawTypes() {
        assertThat(McqSupport.compileErrors("Snippet", wrap("List<Object> a = new ArrayList<String>();"))).isNotEmpty();
        McqSupport.show("the statement does not compile, so it cannot fail at runtime");
    }

    // PURPOSE: Contrast - ARRAYS are covariant (String[] is an Object[]); allowed, but it fails at runtime with ArrayStoreException.
    @Test
    void contrast_arraysAreCovariant() {
        Object[] objects = new String[1];
        McqSupport.attempt("objects[0] = 1  (objects is really String[])", () -> objects[0] = 1);
        assertThatThrownBy(() -> objects[0] = 1).isInstanceOf(ArrayStoreException.class);
    }
}
