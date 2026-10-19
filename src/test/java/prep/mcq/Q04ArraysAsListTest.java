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
 */
class Q04ArraysAsListTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'D';

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - Arrays.asList is a FIXED-SIZE list that is a view of the array: elements can change, size cannot.
    @Test
    void concept_fixedSizeViewOfTheArray() {
        String[] array = {"a", "b", "c"};
        List<String> view = Arrays.asList(array);
        view.set(0, "X");
        assertThat(array[0]).isEqualTo("X");           // writes go through to the array...
        array[1] = "Y";
        assertThat(view).containsExactly("X", "Y", "c"); // ...and array changes show in the list
        assertThatThrownBy(() -> view.add("d")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> view.remove(0)).isInstanceOf(UnsupportedOperationException.class);
    }

    // PURPOSE: Experiment for option A - set() replaces an element; it is allowed (only the size is fixed).
    @Test
    void experimentA_setWorks() {
        List<String> l = Arrays.asList("a", "b", "c");
        assertThat(l.set(0, "x")).isEqualTo("a");      // returns the old element
        assertThat(l).containsExactly("x", "b", "c");
        // Verdict: A does not throw.
    }

    // PURPOSE: Experiment for option B - get() is a read, always fine.
    @Test
    void experimentB_getWorks() {
        assertThat(Arrays.asList("a", "b", "c").get(0)).isEqualTo("a");
        // Verdict: B does not throw.
    }

    // PURPOSE: Experiment for option C - sorting only rearranges elements (a series of sets), so the fixed size is no problem.
    @Test
    void experimentC_sortWorks() {
        List<String> l = Arrays.asList("c", "a", "b");
        l.sort(null);                                  // null comparator = natural order
        assertThat(l).containsExactly("a", "b", "c");
        // Verdict: C does not throw.
    }

    // PURPOSE: Experiment for option D - add() would change the size, which the array-backed list cannot do.
    @Test
    void experimentD_addThrows() {
        List<String> l = Arrays.asList("a", "b", "c");
        assertThatThrownBy(() -> l.add("d")).isInstanceOf(UnsupportedOperationException.class);
        // Verdict: D throws. To get a resizable copy use new ArrayList<>(Arrays.asList(...)).
        List<String> copy = new ArrayList<>(l);
        copy.add("d");
        assertThat(copy).hasSize(4);
    }
}
