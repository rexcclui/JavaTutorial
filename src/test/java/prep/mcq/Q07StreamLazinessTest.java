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
 */
class Q07StreamLazinessTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'B';

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - a counter inside filter() shows WHEN the work happens: not while building the pipeline, only at the terminal operation.
    @Test
    void concept_workHappensAtTheTerminalOperation() {
        AtomicInteger work = new AtomicInteger();
        Stream<Integer> pipeline = Stream.of(1, 2, 3).filter(n -> { work.incrementAndGet(); return true; }).map(n -> n * 2);
        assertThat(work.get()).isEqualTo(0);         // pipeline built, nothing ran
        List<Integer> result = pipeline.toList();    // terminal operation
        assertThat(work.get()).isEqualTo(3);
        assertThat(result).containsExactly(2, 4, 6);
    }

    // PURPOSE: Experiment for option A - are intermediate operations executed when called? Count work after calling only intermediate operations.
    @Test
    void experimentA_nothingRunsWithoutTerminal() {
        AtomicInteger work = new AtomicInteger();
        Stream.of(1, 2, 3).peek(n -> work.incrementAndGet()).filter(n -> n > 1).map(n -> n + 1);
        assertThat(work.get()).isEqualTo(0);
        // Verdict: A is false.
    }

    // PURPOSE: Experiment for option B - laziness even allows short-circuiting: findFirst() stops after the first match, the rest is never processed.
    @Test
    void experimentB_lazyAndShortCircuits() {
        AtomicInteger work = new AtomicInteger();
        int first = Stream.of(10, 20, 30, 40).peek(n -> work.incrementAndGet()).filter(n -> n >= 10).findFirst().get();
        assertThat(first).isEqualTo(10);
        assertThat(work.get()).isEqualTo(1);         // only ONE element was processed, not four
        // Verdict: B is true.
    }

    // PURPOSE: Experiment for option C - a stream can be consumed only once; using it again throws IllegalStateException.
    @Test
    void experimentC_cannotReuseAfterTerminal() {
        Stream<Integer> s = Stream.of(1, 2, 3);
        s.count();                                   // terminal operation: stream is now closed for further use
        assertThatThrownBy(() -> s.map(n -> n + 1)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been operated upon or closed");
        // Verdict: C is false.
    }

    // PURPOSE: Experiment for option D - streams are sequential unless you ask for parallel() / parallelStream().
    @Test
    void experimentD_sequentialByDefault() {
        assertThat(Stream.of(1, 2, 3).isParallel()).isFalse();
        assertThat(List.of(1, 2, 3).stream().isParallel()).isFalse();
        assertThat(List.of(1, 2, 3).parallelStream().isParallel()).isTrue();
        // Verdict: D is false.
    }
}
