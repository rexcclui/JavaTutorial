package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 18 - equals without hashCode
 *
 * A class overrides equals() but NOT hashCode(). Two instances that are equal according to equals() are added to a HashSet.
 * What is the likely result?
 *
 *   A. The set contains one element.
 *   B. An IllegalStateException is thrown.
 *   C. It does not compile: equals() and hashCode() must be overridden together.
 *   D. The set most likely contains both, because they get different hash codes and land in different buckets; contains() may fail to find an equal object.
 *
 * ANSWER: D
 *
 * WHY:
 *   A. Wrong - de-duplication needs equal hash codes first. Default hashCode() is based on object identity.
 *   B. Wrong - nothing checks the contract; the collection just misbehaves silently.
 *   C. Wrong - it compiles (at most an IDE/lint warning).
 *   D. Correct - HashSet looks in the bucket for the hash code and only then calls equals(); the second object hashes elsewhere.
 *
 * See it run:  ./run.sh mcq Q18
 */
class Q18EqualsWithoutHashCodeTest {

    /** equals() overridden, hashCode() NOT. */
    static class Bad {
        final int id;
        Bad(int id) { this.id = id; }
        @Override public boolean equals(Object o) { return o instanceof Bad b && b.id == id; }
    }

    /** Both overridden consistently. */
    static class Good {
        final int id;
        Good(int id) { this.id = id; }
        @Override public boolean equals(Object o) { return o instanceof Good g && g.id == id; }
        @Override public int hashCode() { return Objects.hash(id); }
    }

    // PURPOSE: Demo - same logical object twice, with and without hashCode: the set sizes and contains() results show the difference.
    @Test
    void demo_badVersusGood() {
        Bad b1 = new Bad(1), b2 = new Bad(1);
        McqSupport.show("Bad : b1.equals(b2) = %s,  hashCodes %d vs %d", b1.equals(b2), b1.hashCode(), b2.hashCode());
        Set<Bad> badSet = new HashSet<>(Set.of(b1));
        McqSupport.show("Bad : set.add(b2) -> %s   set size = %d   set.contains(new Bad(1)) = %s", badSet.add(b2), badSet.size(), badSet.contains(new Bad(1)));

        Good g1 = new Good(1), g2 = new Good(1);
        Set<Good> goodSet = new HashSet<>(Set.of(g1));
        McqSupport.show("Good: set.add(g2) -> %s  set size = %d   set.contains(new Good(1)) = %s", goodSet.add(g2), goodSet.size(), goodSet.contains(new Good(1)));
        assertThat(badSet).hasSize(2);
        assertThat(goodSet).hasSize(1);
    }

    // PURPOSE: Option A is WRONG - with only equals() overridden the set keeps BOTH elements.
    @Test
    void optionA_wrong_noDeduplication() {
        Set<Bad> set = new HashSet<>();
        set.add(new Bad(7));
        set.add(new Bad(7));
        McqSupport.show("set size = %d (expected 1 if the contract held)", set.size());
        assertThat(set).hasSize(2);
    }

    // PURPOSE: Option B is WRONG - no exception is thrown; the break is silent, which is what makes it dangerous.
    @Test
    void optionB_wrong_noException() {
        McqSupport.attempt("new HashSet<Bad>() add twice", () -> {
            Set<Bad> set = new HashSet<>();
            set.add(new Bad(1));
            set.add(new Bad(1));
        });
    }

    // PURPOSE: Option C is WRONG - the class compiles (checked with the real compiler).
    @Test
    void optionC_wrong_compiles() {
        String source = """
                public class Snippet {
                    static class Bad {
                        int id;
                        @Override public boolean equals(Object o) { return o instanceof Bad b && b.id == id; }
                    }
                }
                """;
        var errors = McqSupport.compileErrors("Snippet", source);
        McqSupport.show("compiler errors: %s", errors.isEmpty() ? "none - it compiles" : errors);
        assertThat(errors).isEmpty();
    }

    // PURPOSE: Option D is CORRECT - contains() fails for an object that equals() says is present.
    @Test
    void optionD_correct_containsFails() {
        Set<Bad> set = new HashSet<>();
        set.add(new Bad(1));
        boolean found = set.contains(new Bad(1));
        McqSupport.show("set holds Bad(1); set.contains(new Bad(1)) = %s   <- equal object, still not found", found);
        assertThat(found).isFalse();
    }
}
