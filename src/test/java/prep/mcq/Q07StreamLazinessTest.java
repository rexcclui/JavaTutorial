package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 7 - Stream laziness
 *
 * Which statement about intermediate stream operations (filter, map, sorted...) is true?
 *
 *   A. They execute immediately when they are called.
 *   B. They are lazy: nothing is processed until a terminal operation (collect, count, forEach...) is invoked.
 *   C. After a terminal operation you can keep adding intermediate operations to the same stream object.
 *   D. They always run in parallel.
 *
 * ANSWER: B
 *
 * WHY:
 *   A. Wrong - calling filter()/map() only describes the pipeline; no element is touched yet.
 *   B. Correct - the work starts at the terminal operation, element by element, and can stop early (findFirst, limit).
 *   C. Wrong - a stream can be consumed once; reusing it throws IllegalStateException.
 *   D. Wrong - streams are sequential unless you call parallel() / parallelStream().
 *
 * See it run:  ./run.sh mcq Q07
 */
class Q07StreamLazinessTest {

    // PURPOSE: Demo - watch the order of events: building the pipeline prints nothing; toList() then pushes ONE element at a time through filter and map.
    @Test
    void demo_workHappensAtTheTerminalOperation() {
        McqSupport.show("building the pipeline (filter, map) ...");
        Stream<Integer> pipeline = Stream.of(1, 2, 3)
                .filter(n -> { McqSupport.show("    filter(%d)", n); return n != 2; })
                .map(n -> { McqSupport.show("    map(%d)", n); return n * 10; });
        McqSupport.show("...pipeline built, NOTHING has run yet. Now the terminal operation toList():");
        List<Integer> result = pipeline.toList();
        McqSupport.show("result = %s   (note filter and map alternate per element, not 'all filters then all maps')", result);
        assertThat(result).containsExactly(10, 30);
    }

    // PURPOSE: Option A is WRONG - after calling only intermediate operations, the work counter is still 0.
    @Test
    void optionA_wrong_nothingRunsWithoutTerminal() {
        AtomicInteger work = new AtomicInteger();
        Stream.of(1, 2, 3).peek(n -> work.incrementAndGet()).filter(n -> n > 1).map(n -> n + 1);
        McqSupport.show("elements processed after filter/map/peek calls but no terminal op: %d", work.get());
        assertThat(work.get()).isEqualTo(0);
    }

    // PURPOSE: Option B is CORRECT - laziness also allows short-circuiting: findFirst() stops after the first match.
    @Test
    void optionB_correct_lazyAndShortCircuits() {
        AtomicInteger work = new AtomicInteger();
        int first = Stream.of(10, 20, 30, 40).peek(n -> work.incrementAndGet()).filter(n -> n >= 10).findFirst().get();
        McqSupport.show("source has 4 elements, findFirst() = %d, elements actually processed = %d", first, work.get());
        assertThat(work.get()).isEqualTo(1);
    }

    // PURPOSE: Option C is WRONG - a stream can be used only once; after the terminal operation it is closed.
    @Test
    void optionC_wrong_cannotReuseAfterTerminal() {
        Stream<Integer> s = Stream.of(1, 2, 3);
        McqSupport.show("s.count() = %d", s.count());
        McqSupport.attempt("s.map(n -> n + 1) after count()", () -> s.map(n -> n + 1));
        assertThatThrownBy(() -> s.map(n -> n + 1)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been operated upon or closed");
    }

    // PURPOSE: Option D is WRONG - sequential by default; parallel only on request.
    @Test
    void optionD_wrong_sequentialByDefault() {
        McqSupport.show("Stream.of(...).isParallel()          = %s", Stream.of(1, 2, 3).isParallel());
        McqSupport.show("list.stream().isParallel()           = %s", List.of(1, 2, 3).stream().isParallel());
        McqSupport.show("list.parallelStream().isParallel()   = %s", List.of(1, 2, 3).parallelStream().isParallel());
        assertThat(Stream.of(1, 2, 3).isParallel()).isFalse();
        assertThat(List.of(1, 2, 3).parallelStream().isParallel()).isTrue();
    }
}
