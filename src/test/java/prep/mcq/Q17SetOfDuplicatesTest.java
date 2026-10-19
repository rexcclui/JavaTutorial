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
 */
class Q17SetOfDuplicatesTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'C';

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - Set.of treats a duplicate as a programming error and refuses it; Map.of does the same for duplicate keys.
    @Test
    void concept_duplicatesAreRejected() {
        assertThatThrownBy(() -> Set.of("a", "a"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duplicate element");
        assertThatThrownBy(() -> Map.of("k", 1, "k", 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duplicate key");
    }

    // PURPOSE: Experiment for option A - sets that DO silently drop duplicates: HashSet, and Set.copyOf (which tolerates duplicates in its input).
    @Test
    void experimentA_otherWaysDoDeduplicate() {
        assertThat(new HashSet<>(List.of("a", "a"))).hasSize(1);
        assertThat(Set.copyOf(List.of("a", "a"))).containsExactly("a");
        // Verdict: A is false for Set.of(...) itself.
    }

    // PURPOSE: Experiment for option B - NullPointerException is what Set.of throws for a null element, a different mistake.
    @Test
    void experimentB_npeIsForNull() {
        assertThatThrownBy(() -> Set.of("a", null)).isInstanceOf(NullPointerException.class);
        // Verdict: B is false for duplicates.
    }

    // PURPOSE: Experiment for option D - UnsupportedOperationException is what you get when you try to MODIFY the finished set, not when creating it.
    @Test
    void experimentD_uoeIsForModification() {
        Set<String> s = Set.of("a", "b");                 // creation succeeds
        assertThatThrownBy(() -> s.add("c")).isInstanceOf(UnsupportedOperationException.class);
        // Verdict: D is false for creation with duplicates.
    }
}
