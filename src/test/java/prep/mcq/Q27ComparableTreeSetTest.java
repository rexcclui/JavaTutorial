package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 27 - Comparable and TreeSet
 *
 *     class Person { String name; ... }          // does NOT implement Comparable
 *     TreeSet<Person> set = new TreeSet<>();     // no Comparator given
 *     set.add(new Person("Ann"));
 *
 * What happens?
 *
 *   A. The element is stored; the set keeps insertion order.
 *   B. A ClassCastException is thrown at runtime (Person cannot be cast to Comparable).
 *   C. It does not compile.
 *   D. An IllegalArgumentException is thrown.
 *
 * ANSWER: B
 *
 * WHY:
 *   A. Wrong - a TreeSet sorts; it never uses insertion order.
 *   B. Correct - without a Comparator, TreeSet casts each element to Comparable. Since Java 7 even the FIRST add() does this check.
 *   C. Wrong - generics do not require Comparable for TreeSet<T>, so it compiles (the problem shows up at runtime).
 *   D. Wrong - the exception type is ClassCastException.
 *
 * Fix: make Person implement Comparable<Person>, or pass a Comparator: new TreeSet<>(Comparator.comparing(Person::name)).
 *
 * See it run:  ./run.sh mcq Q27
 */
class Q27ComparableTreeSetTest {
    static class Person {
        final String name;
        Person(String name) { this.name = name; }
        @Override public String toString() { return name; }
    }

    static class ComparablePerson implements Comparable<ComparablePerson> {
        final String name;
        ComparablePerson(String name) { this.name = name; }
        @Override public int compareTo(ComparablePerson o) { return name.compareTo(o.name); }
        @Override public String toString() { return name; }
    }

    // PURPOSE: Demo - add to a TreeSet with no Comparator: see the exception and its message.
    @Test
    void demo_theException() {
        TreeSet<Person> set = new TreeSet<>();
        try {
            set.add(new Person("Ann"));
        } catch (ClassCastException e) {
            McqSupport.show("ClassCastException: %s", e.getMessage());
        }
        McqSupport.show("set size afterwards = %d", set.size());
        assertThat(set).isEmpty();
    }

    // PURPOSE: Option B is CORRECT - even the very first element triggers the ClassCastException.
    @Test
    void optionB_correct_firstAddAlreadyFails() {
        McqSupport.attempt("new TreeSet<Person>().add(first)", () -> new TreeSet<Person>().add(new Person("Ann")));
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> new TreeSet<Person>().add(new Person("Ann")))
                .isInstanceOf(ClassCastException.class);
    }

    // PURPOSE: Option C is WRONG - it compiles (real compiler).
    @Test
    void optionC_wrong_compiles() {
        String source = """
                import java.util.*;
                public class Snippet {
                    static class Person { String name; }
                    void m() { TreeSet<Person> set = new TreeSet<>(); set.add(new Person()); }
                }
                """;
        var errors = McqSupport.compileErrors("Snippet", source);
        McqSupport.show("compiler errors: %s", errors.isEmpty() ? "none - it compiles" : errors);
        assertThat(errors).isEmpty();
    }

    // PURPOSE: Fix 1 - implement Comparable: the set now sorts by the natural order you defined.
    @Test
    void fix_implementComparable() {
        TreeSet<ComparablePerson> set = new TreeSet<>();
        set.add(new ComparablePerson("Cid"));
        set.add(new ComparablePerson("Ann"));
        set.add(new ComparablePerson("Bob"));
        McqSupport.show("TreeSet<ComparablePerson> = %s   (sorted, not insertion order)", set);
        assertThat(set).extracting(p -> p.name).containsExactly("Ann", "Bob", "Cid");
    }

    // PURPOSE: Fix 2 - pass a Comparator; works even for classes you cannot modify. Also: the comparator defines "duplicate" (equal compare = same element).
    @Test
    void fix_comparator_andItDefinesDuplicates() {
        TreeSet<Person> set = new TreeSet<>(Comparator.comparing((Person p) -> p.name));
        set.add(new Person("Ann"));
        boolean addedSecondAnn = set.add(new Person("Ann"));
        McqSupport.show("second Person(\"Ann\") added? %s   set = %s   (comparator says equal -> duplicate)", addedSecondAnn, set);
        assertThat(addedSecondAnn).isFalse();
        assertThat(set).hasSize(1);
    }
}
