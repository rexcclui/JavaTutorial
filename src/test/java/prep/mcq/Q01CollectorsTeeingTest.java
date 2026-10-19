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
 * ANSWER: B
 *
 * WHY:
 *   A. Wrong - no parallel streams are created. Every element is handed to BOTH collectors in the same single pass, on the same thread.
 *   B. Correct - collector 1 and collector 2 each build a result; the BiFunction merges them into one value.
 *   C. Wrong - teeing does no filtering. Duplicates stay; use distinct() or a Set collector to remove them.
 *   D. Wrong - the signature is (Collector, Collector, BiFunction). No file, socket or logging is involved. The name comes from the Unix 'tee' command.
 *
 * NOTE: teeing() was added in Java 12, not 11 (small mistakes like this appear in real tests; pick the best-fitting option).
 *
 * Picture:
 *                +--> collector 1 --+
 *     elements --+                  +--> merger (BiFunction) --> ONE result
 *                +--> collector 2 --+
 *
 * See it run:  ./run.sh mcq Q01     (what each demo prints is shown under its test)
 */
class Q01CollectorsTeeingTest {
    record Range(int min, int max) {}

    // PURPOSE: Demo - min and max of the same data computed together in ONE pass, then merged into a Range.
    @Test
    void demo_minAndMaxInOnePass() {
        McqSupport.show("input            : [5, 3, 9, 1]");
        Range range = Stream.of(5, 3, 9, 1).collect(teeing(
                minBy(Comparator.<Integer>naturalOrder()),
                maxBy(Comparator.<Integer>naturalOrder()),
                (min, max) -> {
                    McqSupport.show("collector 1 result: min = %s", min);
                    McqSupport.show("collector 2 result: max = %s", max);
                    return new Range(min.get(), max.get());
                }));
        McqSupport.show("merged result    : %s", range);
        assertThat(range).isEqualTo(new Range(1, 9));
    }

    // PURPOSE: Demo - the classic use: average = sum / count, computed in a single pass.
    @Test
    void demo_averageFromSumAndCount() {
        double avg = Stream.of(5, 3, 9, 1).collect(teeing(
                summingInt(i -> i),
                counting(),
                (sum, count) -> {
                    McqSupport.show("sum = %d, count = %d  ->  average = sum / count", sum, count);
                    return (double) sum / count;
                }));
        assertThat(avg).isEqualTo(4.5);
    }

    // PURPOSE: Option A is WRONG - measure it: not parallel, each element visited once although two collectors consume it, all on the calling thread.
    @Test
    void optionA_wrong_noParallelismOnlyOneTraversal() {
        Set<String> threads = ConcurrentHashMap.newKeySet();
        AtomicInteger visits = new AtomicInteger();
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5).peek(x -> {
            visits.incrementAndGet();
            threads.add(Thread.currentThread().getName());
        });
        McqSupport.show("stream.isParallel()  = %s", stream.isParallel());
        assertThat(stream.isParallel()).isFalse();
        stream.collect(teeing(counting(), summingInt(i -> i), (n, sum) -> n + sum));
        McqSupport.show("elements in source   = 5, two collectors, element visits = %d  (ONE pass)", visits.get());
        McqSupport.show("threads used         = %s  (calling thread: %s)", threads, Thread.currentThread().getName());
        assertThat(visits.get()).isEqualTo(5);
        assertThat(threads).containsExactly(Thread.currentThread().getName());
    }

    // PURPOSE: Option B is CORRECT - two results merged by a BiFunction into one value.
    @Test
    void optionB_correct_mergesTwoResultsWithABiFunction() {
        String merged = Stream.of("a", "b", "c").collect(teeing(
                counting(),
                joining(","),
                (count, text) -> count + " items: " + text));
        McqSupport.show("counting()  -> 3");
        McqSupport.show("joining(\",\") -> a,b,c");
        McqSupport.show("merger      -> \"%s\"", merged);
        assertThat(merged).isEqualTo("3 items: a,b,c");
    }

    // PURPOSE: Option C is WRONG - duplicates are not removed; teeing does not filter.
    @Test
    void optionC_wrong_doesNotRemoveDuplicates() {
        var list = Stream.of(1, 1, 2).collect(teeing(toList(), counting(), (l, n) -> l));
        McqSupport.show("input [1, 1, 2]  -> list seen by the collector: %s  (duplicates still there)", list);
        assertThat(list).containsExactly(1, 1, 2);
    }

    // PURPOSE: Option D is WRONG - teeing's only signature has no file, socket or logging parameter (checked by reflection).
    @Test
    void optionD_wrong_signatureHasNoIoParameters() {
        var methods = Arrays.stream(Collectors.class.getMethods()).filter(m -> m.getName().equals("teeing")).toList();
        McqSupport.show("methods named teeing : %d", methods.size());
        for (Class<?> type : methods.get(0).getParameterTypes()) McqSupport.show("  parameter type      : %s", type.getSimpleName());
        assertThat(methods).hasSize(1);
        assertThat(methods.get(0).getParameterTypes()).containsExactly(Collector.class, Collector.class, BiFunction.class);
    }
}
