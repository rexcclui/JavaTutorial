package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 9 - List.of
 *
 * Which statement about List.of(...) (Java 9+) is correct?
 *
 *   A. It returns a mutable list backed by an array.
 *   B. It allows null elements.
 *   C. It returns an immutable list: add/set/remove throw UnsupportedOperationException, and null elements are rejected with NullPointerException.
 *   D. It returns a synchronized (thread-safe wrapper) list.
 */
class Q09ListOfImmutableTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'C';

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - try every kind of modification on List.of(...).
    @Test
    void concept_everyModificationFails() {
        List<String> l = List.of("a", "b");
        assertThatThrownBy(() -> l.add("c")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> l.set(0, "x")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> l.remove(0)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(l::clear).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> l.sort(null)).isInstanceOf(UnsupportedOperationException.class);
    }

    // PURPOSE: Experiment for option A - contrast with Arrays.asList (writes allowed) to show List.of does not even allow set().
    @Test
    void experimentA_notMutable() {
        assertThat(Arrays.asList("a", "b").set(0, "x")).isEqualTo("a");  // Arrays.asList: set works
        assertThatThrownBy(() -> List.of("a", "b").set(0, "x")).isInstanceOf(UnsupportedOperationException.class);
        // Verdict: A is false.
    }

    // PURPOSE: Experiment for option B - null elements are rejected when creating the list (and even when asking contains(null)).
    @Test
    void experimentB_nullsRejected() {
        assertThatThrownBy(() -> List.of("a", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> List.of("a").contains(null)).isInstanceOf(NullPointerException.class);
        assertThat(Arrays.asList("a", null)).hasSize(2);       // Arrays.asList and ArrayList do allow null
        // Verdict: B is false.
    }

    // PURPOSE: Experiment for option C - immutable, and it is a snapshot: later changes to the source list do not show up (unlike an unmodifiable view).
    @Test
    void experimentC_immutableSnapshot() {
        List<String> source = new ArrayList<>(List.of("a", "b"));
        List<String> copy = List.copyOf(source);                          // immutable snapshot
        List<String> view = Collections.unmodifiableList(source);         // read-only VIEW
        source.add("c");
        assertThat(copy).containsExactly("a", "b");
        assertThat(view).containsExactly("a", "b", "c");                  // the view sees the change
        // Verdict: C is true. Immutable means nobody can change it, so it is safe to share between threads without locking.
    }

    // PURPOSE: Experiment for option D - List.of is not a synchronized wrapper; it is a different, immutable implementation class.
    @Test
    void experimentD_notASynchronizedWrapper() {
        Class<?> immutable = List.of("a").getClass();
        Class<?> synced = Collections.synchronizedList(new ArrayList<String>()).getClass();
        assertThat(immutable).isNotEqualTo(synced);
        assertThat(synced.getSimpleName()).contains("Synchronized");
        assertThat(immutable.getSimpleName()).doesNotContain("Synchronized");
        // Verdict: D is false.
    }
}
