package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 4 - Arrays.asList
 *
 * Given:  List<String> l = Arrays.asList("a", "b", "c");
 * Which of these calls throws UnsupportedOperationException?
 *
 *   A. l.set(0, "x")
 *   B. l.get(0)
 *   C. l.sort(null)
 *   D. l.add("d")
 *
 * ANSWER: D
 *
 * WHY:
 *   A. Does not throw - set() replaces an element; only the SIZE is fixed.
 *   B. Does not throw - get() is a read.
 *   C. Does not throw - sorting only rearranges elements (a series of set() calls).
 *   D. THROWS - add() would change the size, but the list is a fixed-size view of an array.
 *
 * See it run:  ./run.sh mcq Q04
 */
class Q04ArraysAsListTest {

    // PURPOSE: Demo - Arrays.asList is a fixed-size VIEW of the array: element changes go both ways, size changes are forbidden.
    @Test
    void demo_fixedSizeViewOfTheArray() {
        String[] array = {"a", "b", "c"};
        List<String> view = Arrays.asList(array);
        McqSupport.show("array = %s   list = %s", Arrays.toString(array), view);

        view.set(0, "X");
        McqSupport.show("view.set(0, \"X\")  -> array = %s   (write goes through to the array)", Arrays.toString(array));
        array[1] = "Y";
        McqSupport.show("array[1] = \"Y\"    -> list  = %s   (array change shows in the list)", view);

        McqSupport.attempt("view.add(\"d\")", () -> view.add("d"));
        McqSupport.attempt("view.remove(0)", () -> view.remove(0));
        assertThat(array[0]).isEqualTo("X");
        assertThat(view).containsExactly("X", "Y", "c");
    }

    // PURPOSE: Option A does NOT throw - set() works.
    @Test
    void optionA_noThrow_setWorks() {
        List<String> l = Arrays.asList("a", "b", "c");
        McqSupport.attempt("l.set(0, \"x\")", () -> l.set(0, "x"));
        assertThat(l).containsExactly("x", "b", "c");
    }

    // PURPOSE: Option B does NOT throw - get() works.
    @Test
    void optionB_noThrow_getWorks() {
        List<String> l = Arrays.asList("a", "b", "c");
        McqSupport.attempt("l.get(0)", () -> l.get(0));
        assertThat(l.get(0)).isEqualTo("a");
    }

    // PURPOSE: Option C does NOT throw - sort() works because it only rearranges elements.
    @Test
    void optionC_noThrow_sortWorks() {
        List<String> l = Arrays.asList("c", "a", "b");
        l.sort(null);
        McqSupport.show("after l.sort(null) -> %s", l);
        assertThat(l).containsExactly("a", "b", "c");
    }

    // PURPOSE: Option D is the ANSWER - add() throws UnsupportedOperationException. A resizable copy needs new ArrayList<>(...).
    @Test
    void optionD_correct_addThrows() {
        List<String> l = Arrays.asList("a", "b", "c");
        McqSupport.attempt("l.add(\"d\")", () -> l.add("d"));
        assertThatThrownBy(() -> l.add("d")).isInstanceOf(UnsupportedOperationException.class);

        List<String> copy = new ArrayList<>(l);
        copy.add("d");
        McqSupport.show("fix: new ArrayList<>(l).add(\"d\") -> %s", copy);
        assertThat(copy).hasSize(4);
    }
}
