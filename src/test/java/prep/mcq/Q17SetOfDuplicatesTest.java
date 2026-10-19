package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 17 - Set.of with duplicates
 *
 *     Set<String> s = Set.of("a", "a");
 *
 * What happens?
 *
 *   A. It returns a set containing one element, "a".
 *   B. It throws NullPointerException.
 *   C. It throws IllegalArgumentException (duplicate element).
 *   D. It throws UnsupportedOperationException.
 *
 * ANSWER: C
 *
 * WHY:
 *   A. Wrong - Set.of treats a duplicate as a programming error. (HashSet and Set.copyOf DO silently drop duplicates.)
 *   B. Wrong - NullPointerException is what Set.of throws for a null element.
 *   C. Correct - "duplicate element: a". Map.of does the same for duplicate keys.
 *   D. Wrong - UnsupportedOperationException is what you get when you try to MODIFY the finished immutable set.
 *
 * See it run:  ./run.sh mcq Q17
 */
class Q17SetOfDuplicatesTest {

    // PURPOSE: Demo - see the exact exception message for Set.of and Map.of duplicates.
    @Test
    void demo_duplicatesAreRejected() {
        try {
            Set.of("a", "a");
        } catch (IllegalArgumentException e) {
            McqSupport.show("Set.of(\"a\", \"a\")        -> %s: %s", e.getClass().getSimpleName(), e.getMessage());
        }
        try {
            Map.of("k", 1, "k", 2);
        } catch (IllegalArgumentException e) {
            McqSupport.show("Map.of(\"k\",1,\"k\",2)    -> %s: %s", e.getClass().getSimpleName(), e.getMessage());
        }
        assertThatThrownBy(() -> Set.of("a", "a")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("duplicate element");
        assertThatThrownBy(() -> Map.of("k", 1, "k", 2)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("duplicate key");
    }

    // PURPOSE: Option A is WRONG for Set.of - but these ways DO remove duplicates silently.
    @Test
    void optionA_wrong_otherWaysDeduplicate() {
        McqSupport.show("new HashSet<>(List.of(\"a\",\"a\")) = %s", new HashSet<>(List.of("a", "a")));
        McqSupport.show("Set.copyOf(List.of(\"a\",\"a\"))    = %s", Set.copyOf(List.of("a", "a")));
        assertThat(new HashSet<>(List.of("a", "a"))).hasSize(1);
        assertThat(Set.copyOf(List.of("a", "a"))).containsExactly("a");
    }

    // PURPOSE: Option B is WRONG - NullPointerException is for a null element, a different mistake.
    @Test
    void optionB_wrong_npeIsForNull() {
        McqSupport.attempt("Set.of(\"a\", null)", () -> Set.of("a", null));
        assertThatThrownBy(() -> Set.of("a", null)).isInstanceOf(NullPointerException.class);
    }

    // PURPOSE: Option D is WRONG - UnsupportedOperationException happens when you MODIFY the set, not when creating it.
    @Test
    void optionD_wrong_uoeIsForModification() {
        Set<String> s = Set.of("a", "b");
        McqSupport.show("creating Set.of(\"a\", \"b\") worked: %d elements", s.size());
        McqSupport.attempt("s.add(\"c\")", () -> s.add("c"));
        assertThatThrownBy(() -> s.add("c")).isInstanceOf(UnsupportedOperationException.class);
    }
}
