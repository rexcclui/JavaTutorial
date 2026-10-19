package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 29 - map vs flatMap
 *
 * What does Stream.flatMap(f) do?
 *
 *   A. It replaces each element by the stream returned by f and concatenates ("flattens") all those streams into one.
 *   B. It is the same as map(f) but runs in parallel.
 *   C. It reduces the stream to a single value.
 *   D. It removes null elements.
 *
 * ANSWER: A
 *
 * WHY:
 *   map: one input -> exactly one output (so a function returning a list gives a Stream<List<X>>).
 *   flatMap: one input -> ZERO OR MORE outputs (f returns a stream), all merged into a single flat stream.
 *   B. Parallelism has nothing to do with it. C. That is reduce/collect. D. That is filter(Objects::nonNull).
 *
 * See it run:  ./run.sh mcq Q29
 */
class Q29FlatMapTest {
    private static final List<List<Integer>> NESTED = List.of(List.of(1, 2), List.of(3), List.of(), List.of(4, 5));

    // PURPOSE: Demo - the same nested data with map (stays nested) and flatMap (flattened).
    @Test
    void demo_mapVersusFlatMap() {
        List<Stream<Integer>> mapped = NESTED.stream().map(List::stream).toList();
        List<Integer> flat = NESTED.stream().flatMap(List::stream).toList();
        McqSupport.show("input                 : %s", NESTED);
        McqSupport.show("map(List::stream)     : a Stream of %d streams (still nested)", mapped.size());
        McqSupport.show("flatMap(List::stream) : %s", flat);
        assertThat(mapped).hasSize(4);
        assertThat(flat).containsExactly(1, 2, 3, 4, 5);
    }

    // PURPOSE: Option A is CORRECT - one-to-many: each word becomes several letters; all letters end up in one stream.
    @Test
    void optionA_correct_oneToMany() {
        List<String> letters = Stream.of("hi", "yo").flatMap(w -> w.chars().mapToObj(c -> String.valueOf((char) c))).toList();
        McqSupport.show("words [hi, yo] -> letters %s", letters);
        assertThat(letters).containsExactly("h", "i", "y", "o");
    }

    // PURPOSE: Option B is WRONG - flatMap is sequential here; it is only about flattening.
    @Test
    void optionB_wrong_notAboutParallelism() {
        assertThat(NESTED.stream().flatMap(List::stream).isParallel()).isFalse();
    }

    // PURPOSE: Option C is WRONG - flatMap does not reduce; the number of elements can grow, shrink or stay.
    @Test
    void optionC_wrong_doesNotReduce() {
        McqSupport.show("1 element -> %d elements; 1 element -> %d elements",
                Stream.of("a").flatMap(s -> Stream.of(s, s, s)).count(), Stream.of("a").flatMap(s -> Stream.<String>empty()).count());
        assertThat(Stream.of("a").flatMap(s -> Stream.of(s, s, s)).count()).isEqualTo(3);
        assertThat(Stream.of("a").flatMap(s -> Stream.<String>empty()).count()).isEqualTo(0);
    }

    // PURPOSE: Option D is WRONG - but flatMap with an empty stream is a neat way to DROP elements, e.g. unwrap Optionals.
    @Test
    void optionD_wrong_butEmptyStreamsDropElements() {
        List<Optional<String>> optionals = List.of(Optional.of("a"), Optional.empty(), Optional.of("c"));
        List<String> present = optionals.stream().flatMap(Optional::stream).toList();
        McqSupport.show("%s -> flatMap(Optional::stream) -> %s", optionals, present);
        assertThat(present).containsExactly("a", "c");
    }

    // PURPOSE: Same idea in Optional: flatMap avoids Optional<Optional<T>>.
    @Test
    void sameIdeaInOptional() {
        Optional<String> name = Optional.of("  ann ");
        Optional<Optional<String>> nested = name.map(s -> s.isBlank() ? Optional.<String>empty() : Optional.of(s.trim()));
        Optional<String> flat = name.flatMap(s -> s.isBlank() ? Optional.<String>empty() : Optional.of(s.trim()));
        McqSupport.show("map     -> %s", nested);
        McqSupport.show("flatMap -> %s", flat);
        assertThat(flat).contains("ann");
    }
}
