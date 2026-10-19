package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 10 - removing from a list while looping
 *
 * Which approach safely removes elements from an ArrayList while traversing it?
 *
 *   A. Using Iterator.remove() (or list.removeIf(...)).
 *   B. A for-each loop that calls list.remove(element) - this always works.
 *   C. list.stream().forEach(list::remove)
 *   D. Collections.unmodifiableList(list).remove(element)
 */
class Q10ConcurrentModificationTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'A';

    private static List<String> abc() {
        return new ArrayList<>(List.of("a", "b", "c"));
    }

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - the iterator knows about its own removals; the list's modCount stays in sync, so no exception.
    @Test
    void concept_iteratorRemove() {
        List<String> list = abc();
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            if (it.next().equals("a")) it.remove();
        }
        assertThat(list).containsExactly("b", "c");
    }

    // PURPOSE: Experiment for option A - both Iterator.remove() and removeIf work and are the idiomatic ways.
    @Test
    void experimentA_iteratorAndRemoveIfWork() {
        List<String> byIterator = abc();
        Iterator<String> it = byIterator.iterator();
        while (it.hasNext()) if (!it.next().equals("b")) it.remove();
        assertThat(byIterator).containsExactly("b");

        List<String> byRemoveIf = abc();
        byRemoveIf.removeIf(s -> s.equals("b"));
        assertThat(byRemoveIf).containsExactly("a", "c");
        // Verdict: A is true.
    }

    // PURPOSE: Experiment for option B - removing inside for-each usually throws ConcurrentModificationException... but NOT always (famous trap).
    @Test
    void experimentB_forEachRemove_usuallyThrows() {
        assertThatThrownBy(() -> {
            List<String> list = abc();
            for (String s : list) if (s.equals("a")) list.remove(s);
        }).isInstanceOf(ConcurrentModificationException.class);

        // The trap: removing the SECOND-TO-LAST element makes hasNext() return false before next() can notice. No exception, but silently wrong.
        List<String> list = abc();
        for (String s : list) if (s.equals("b")) list.remove(s);
        assertThat(list).containsExactly("a", "c");
        // Verdict: B is false - "always works" is wrong; it throws in most cases and is only accidentally fine in this special one.
    }

    // PURPOSE: Experiment for option C - modifying the source from inside a stream also fails (streams do not support interfering with their source).
    @Test
    void experimentC_streamForEachRemove() {
        List<String> list = abc();
        assertThatThrownBy(() -> list.stream().forEach(list::remove)).isInstanceOf(ConcurrentModificationException.class);
        // Verdict: C is false.
    }

    // PURPOSE: Experiment for option D - an unmodifiable wrapper rejects every change, so remove() throws UnsupportedOperationException.
    @Test
    void experimentD_unmodifiableCannotRemove() {
        List<String> view = Collections.unmodifiableList(abc());
        assertThatThrownBy(() -> view.remove("a")).isInstanceOf(UnsupportedOperationException.class);
        // Verdict: D is false.
    }
}
