package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 1 - Collectors.teeing()
 *
 * What is the purpose of the Collectors.teeing() collector introduced in Java 11?
 *
 *   A. It splits a single stream into two separate parallel streams and executes them concurrently on different CPU cores.
 *   B. It wraps two separate collectors, merges their results using a bi-function, and returns a combined result from a single stream pipeline.
 *   C. It filters out duplicate elements from a stream based on two distinct fields.
 *   D. It tees stream logging data directly into an external file system or network socket.
 *
 * HOW TO USE: 1) Decide your answer WITHOUT reading below.  2) Set MY_ANSWER.
 *             3) ./run.sh mcq Q01   4) Read the experiment tests: each gathers evidence about one option.
 *
 * NOTE: teeing() was actually added in Java 12, not 11. Real tests sometimes contain small mistakes like
 * this; pick the option that best fits the concept.
 */
class Q01CollectorsTeeingTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'B';

    record Range(int min, int max) {}

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - two collectors (min and max) fed by ONE pass over the data, results combined by a function.
    @Test
    void concept_minAndMaxInOnePass() {
        Range range = Stream.of(5, 3, 9, 1).collect(teeing(
                minBy(Comparator.<Integer>naturalOrder()),
                maxBy(Comparator.<Integer>naturalOrder()),
                (min, max) -> new Range(min.get(), max.get())));
        assertThat(range).isEqualTo(new Range(1, 9));
    }

    // PURPOSE: Concept - the classic use: average = sum / count, computed in a single pass.
    @Test
    void concept_averageFromSumAndCount() {
        double avg = Stream.of(5, 3, 9, 1).collect(teeing(
                summingInt(i -> i),
                counting(),
                (sum, count) -> (double) sum / count));
        assertThat(avg).isEqualTo(4.5);
    }

    // PURPOSE: Experiment for option A - are the data processed in parallel on several threads? Measure threads used and how often each element is visited.
    @Test
    void experimentA_noParallelismAndOnlyOneTraversal() {
        Set<String> threads = ConcurrentHashMap.newKeySet();
        AtomicInteger visits = new AtomicInteger();
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5).peek(x -> {
            visits.incrementAndGet();
            threads.add(Thread.currentThread().getName());
        });
        assertThat(stream.isParallel()).isFalse();
        stream.collect(teeing(counting(), summingInt(i -> i), (n, sum) -> n + sum));

        assertThat(visits.get()).isEqualTo(5);                                   // each element seen ONCE, though two collectors consume it
        assertThat(threads).containsExactly(Thread.currentThread().getName());  // everything ran on the calling thread
        // Verdict: A is false. teeing does not create parallel streams; it feeds each element to both collectors in the same pass.
    }

    // PURPOSE: Experiment for option B - the result of both collectors is merged by the BiFunction into one value.
    @Test
    void experimentB_mergesTwoResultsWithABiFunction() {
        String merged = Stream.of("a", "b", "c").collect(teeing(
                counting(),
                joining(","),
                (count, text) -> count + " items: " + text));
        assertThat(merged).isEqualTo("3 items: a,b,c");
        // Verdict: B is true - exactly the definition of teeing().
    }

    // PURPOSE: Experiment for option C - does teeing remove duplicates? Feed it duplicates and look at what a collector inside still sees.
    @Test
    void experimentC_doesNotRemoveDuplicates() {
        var list = Stream.of(1, 1, 2).collect(teeing(toList(), counting(), (l, n) -> l));
        assertThat(list).containsExactly(1, 1, 2);   // duplicates are still there
        // Verdict: C is false. To drop duplicates use distinct() or a Set collector, not teeing.
    }

    // PURPOSE: Experiment for option D - look at teeing's method signature using reflection: is there any file, socket or logging parameter?
    @Test
    void experimentD_signatureHasNoIoParameters() {
        var methods = Arrays.stream(Collectors.class.getMethods()).filter(m -> m.getName().equals("teeing")).toList();
        assertThat(methods).hasSize(1);
        assertThat(methods.get(0).getParameterTypes()).containsExactly(Collector.class, Collector.class, BiFunction.class);
        // Verdict: D is false. Two collectors and a merge function - nothing about files, sockets or logging.
        // (The name comes from the Unix 'tee' command, which copies data to two places; that is the only link to "tee".)
    }
}
