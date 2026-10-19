package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 22 - final reference vs immutable object
 *
 *     final List<String> list = new ArrayList<>();
 *
 * Which statement is true?
 *
 *   A. list.add("x") does not compile because list is final.
 *   B. Both list.add("x") and list = new ArrayList<>() compile.
 *   C. Neither list.add("x") nor list = new ArrayList<>() compiles.
 *   D. list.add("x") works, but list = new ArrayList<>() is a compile error.
 *
 * ANSWER: D
 *
 * WHY:
 *   `final` makes the VARIABLE a constant reference (it can never point to another object). It says nothing about the object's contents.
 *   For an immutable collection you need List.of(...) / List.copyOf(...) / Collections.unmodifiableList(...).
 *
 * See it run:  ./run.sh mcq Q22
 */
class Q22FinalCollectionTest {

    private static String wrap(String body) {
        return "import java.util.*; public class Snippet { void m() { final List<String> list = new ArrayList<>(); " + body + " } }";
    }

    // PURPOSE: Demo - a final list can still grow and shrink: the object is not frozen, only the variable.
    @Test
    void demo_finalListIsStillMutable() {
        final List<String> list = new ArrayList<>();
        list.add("x");
        list.add("y");
        list.remove("x");
        McqSupport.show("final list after add, add, remove -> %s", list);
        assertThat(list).containsExactly("y");
    }

    // PURPOSE: Options A and C are WRONG - list.add("x") compiles (real compiler).
    @Test
    void optionAandC_wrong_addCompiles() {
        var errors = McqSupport.compileErrors("Snippet", wrap("list.add(\"x\");"));
        McqSupport.show("list.add(\"x\") compile errors: %s", errors.isEmpty() ? "none" : errors);
        assertThat(errors).isEmpty();
    }

    // PURPOSE: Option D is CORRECT (and B is WRONG) - reassigning a final variable is a compile error.
    @Test
    void optionBandD_reassignIsACompileError() {
        var errors = McqSupport.compileErrors("Snippet", wrap("list = new ArrayList<>();"));
        McqSupport.show("list = new ArrayList<>() compile errors: %s", errors);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("cannot assign a value to final variable list");
    }

    // PURPOSE: Contrast - an IMMUTABLE list: here the object itself refuses changes, whether or not the variable is final.
    @Test
    void contrast_immutableListRejectsChanges() {
        List<String> immutable = List.of("a");
        McqSupport.attempt("List.of(\"a\").add(\"x\")", () -> immutable.add("x"));
        List<String> nonFinalVariable = new ArrayList<>();
        nonFinalVariable = List.of("b");                       // variable is not final, so reassigning is fine
        McqSupport.show("a non-final variable can be re-pointed: %s", nonFinalVariable);
        assertThat(nonFinalVariable).containsExactly("b");
    }
}
