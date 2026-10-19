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
 *
 * ANSWER: C
 *
 * WHY:
 *   A. Wrong - even set() throws (unlike Arrays.asList).
 *   B. Wrong - List.of("a", null) throws NullPointerException.
 *   C. Correct - immutable, null-hostile; safe to share between threads because it can never change.
 *   D. Wrong - it is an immutable implementation class, not a synchronized wrapper.
 *
 * See it run:  ./run.sh mcq Q09
 */
class Q09ListOfImmutableTest {

    // PURPOSE: Demo - try every kind of modification on List.of(...) and see which exception each one gives.
    @Test
    void demo_everyModificationFails() {
        List<String> l = List.of("a", "b");
        McqSupport.attempt("l.add(\"c\")", () -> l.add("c"));
        McqSupport.attempt("l.set(0, \"x\")", () -> l.set(0, "x"));
        McqSupport.attempt("l.remove(0)", () -> l.remove(0));
        McqSupport.attempt("l.clear()", l::clear);
        McqSupport.attempt("l.sort(null)", () -> l.sort(null));
        McqSupport.attempt("l.get(0) (a read)", () -> l.get(0));
        assertThatThrownBy(() -> l.add("c")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> l.set(0, "x")).isInstanceOf(UnsupportedOperationException.class);
    }

    // PURPOSE: Option A is WRONG - contrast with Arrays.asList where set() works; List.of does not even allow that.
    @Test
    void optionA_wrong_notMutable() {
        McqSupport.attempt("Arrays.asList(...).set(0, x)", () -> Arrays.asList("a", "b").set(0, "x"));
        McqSupport.attempt("List.of(...).set(0, x)", () -> List.of("a", "b").set(0, "x"));
        assertThatThrownBy(() -> List.of("a", "b").set(0, "x")).isInstanceOf(UnsupportedOperationException.class);
    }

    // PURPOSE: Option B is WRONG - nulls are rejected when creating the list, and even by contains(null).
    @Test
    void optionB_wrong_nullsRejected() {
        McqSupport.attempt("List.of(\"a\", null)", () -> List.of("a", null));
        McqSupport.attempt("List.of(\"a\").contains(null)", () -> List.of("a").contains(null));
        McqSupport.attempt("Arrays.asList(\"a\", null)", () -> Arrays.asList("a", null));
        McqSupport.attempt("new ArrayList<>().add(null)", () -> new ArrayList<String>().add(null));
        assertThatThrownBy(() -> List.of("a", null)).isInstanceOf(NullPointerException.class);
        assertThat(Arrays.asList("a", null)).hasSize(2);
    }

    // PURPOSE: Option C is CORRECT - and List.copyOf is an immutable SNAPSHOT, unlike unmodifiableList which is a read-only VIEW.
    @Test
    void optionC_correct_immutableSnapshotVersusView() {
        List<String> source = new ArrayList<>(List.of("a", "b"));
        List<String> copy = List.copyOf(source);
        List<String> view = Collections.unmodifiableList(source);
        source.add("c");
        McqSupport.show("source after add(\"c\")                 : %s", source);
        McqSupport.show("List.copyOf(source)  (snapshot)         : %s   <- unchanged", copy);
        McqSupport.show("Collections.unmodifiableList (view)     : %s   <- sees the change", view);
        assertThat(copy).containsExactly("a", "b");
        assertThat(view).containsExactly("a", "b", "c");
    }

    // PURPOSE: Option D is WRONG - List.of is a different (immutable) class than a synchronized wrapper.
    @Test
    void optionD_wrong_notASynchronizedWrapper() {
        Class<?> immutable = List.of("a").getClass();
        Class<?> synced = Collections.synchronizedList(new ArrayList<String>()).getClass();
        McqSupport.show("List.of(...)                       is %s", immutable.getName());
        McqSupport.show("Collections.synchronizedList(...)  is %s", synced.getName());
        assertThat(immutable).isNotEqualTo(synced);
        assertThat(synced.getSimpleName()).contains("Synchronized");
    }
}
