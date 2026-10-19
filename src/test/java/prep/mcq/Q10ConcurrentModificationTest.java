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
 *
 * ANSWER: A
 *
 * WHY:
 *   A. Correct - the iterator updates the list's modification counter itself, so it stays in sync.
 *   B. Wrong - usually throws ConcurrentModificationException. (It silently "works" only when you remove the
 *      second-to-last element, because hasNext() returns false first - a nasty accident, not a guarantee.)
 *   C. Wrong - modifying the source of a stream while it runs also throws ConcurrentModificationException.
 *   D. Wrong - an unmodifiable wrapper rejects every change with UnsupportedOperationException.
 *
 * See it run:  ./run.sh mcq Q10
 */
class Q10ConcurrentModificationTest {

    private static List<String> abc() {
        return new ArrayList<>(List.of("a", "b", "c"));
    }

    // PURPOSE: Demo - remove "a" with an iterator and print the list as it changes.
    @Test
    void demo_iteratorRemove() {
        List<String> list = abc();
        McqSupport.show("before      : %s", list);
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            String s = it.next();
            if (s.equals("a")) {
                it.remove();
                McqSupport.show("it.remove() after seeing \"%s\" -> %s", s, list);
            }
        }
        assertThat(list).containsExactly("b", "c");
    }

    // PURPOSE: Option A is CORRECT - Iterator.remove() and removeIf both work.
    @Test
    void optionA_correct_iteratorAndRemoveIfWork() {
        List<String> byIterator = abc();
        Iterator<String> it = byIterator.iterator();
        while (it.hasNext()) if (!it.next().equals("b")) it.remove();
        McqSupport.show("Iterator.remove() keeping only \"b\"   : %s", byIterator);
        assertThat(byIterator).containsExactly("b");

        List<String> byRemoveIf = abc();
        byRemoveIf.removeIf(s -> s.equals("b"));
        McqSupport.show("removeIf(s -> s.equals(\"b\"))         : %s", byRemoveIf);
        assertThat(byRemoveIf).containsExactly("a", "c");
    }

    // PURPOSE: Option B is WRONG - for-each + list.remove usually throws; the second-to-last element is the silent exception to the rule.
    @Test
    void optionB_wrong_forEachRemoveUsuallyThrows() {
        assertThatThrownBy(() -> {
            List<String> list = abc();
            for (String s : list) if (s.equals("a")) list.remove(s);
        }).isInstanceOf(ConcurrentModificationException.class);
        McqSupport.show("remove \"a\" (first) in for-each          -> ConcurrentModificationException");

        List<String> list = abc();
        for (String s : list) if (s.equals("b")) list.remove(s);
        McqSupport.show("remove \"b\" (second-to-last) in for-each -> no exception, list = %s  (accident: loop ended early)", list);
        assertThat(list).containsExactly("a", "c");
    }

    // PURPOSE: Option C is WRONG - changing the source inside stream().forEach throws too.
    @Test
    void optionC_wrong_streamForEachRemove() {
        List<String> list = abc();
        McqSupport.attempt("list.stream().forEach(list::remove)", () -> list.stream().forEach(list::remove));
        List<String> l2 = abc();
        assertThatThrownBy(() -> l2.stream().forEach(l2::remove)).isInstanceOf(ConcurrentModificationException.class);
    }

    // PURPOSE: Option D is WRONG - an unmodifiable view rejects all changes.
    @Test
    void optionD_wrong_unmodifiableCannotRemove() {
        List<String> view = Collections.unmodifiableList(abc());
        McqSupport.attempt("unmodifiableList(list).remove(\"a\")", () -> view.remove("a"));
        assertThatThrownBy(() -> view.remove("a")).isInstanceOf(UnsupportedOperationException.class);
    }
}
