package prep.concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class ParallelSumTest {
    // PURPOSE: The multi-threaded sum equals the single-threaded sum for different thread counts (including more threads than sensible chunks).
    @Test
    void matchesSequentialSum() throws Exception {
        int[] data = IntStream.rangeClosed(1, 1_000_001).toArray();
        long expected = IntStream.of(data).asLongStream().sum();
        for (int threads : new int[] {1, 2, 3, 7, 16}) {
            assertThat(ParallelSum.sum(data, threads)).isEqualTo(expected);
        }
    }

    // PURPOSE: Edge cases: empty array and an array smaller than the number of threads.
    @Test
    void emptyAndTiny() throws Exception {
        assertThat(ParallelSum.sum(new int[] {}, 4)).isEqualTo(0L);
        assertThat(ParallelSum.sum(new int[] {5}, 8)).isEqualTo(5L);
    }

    // PURPOSE: Accumulate in a long, otherwise the int sum overflows.
    @Test
    void largeValuesDoNotOverflowInt() throws Exception {
        int[] data = new int[10];
        java.util.Arrays.fill(data, Integer.MAX_VALUE);
        assertThat(ParallelSum.sum(data, 3)).isEqualTo(10L * Integer.MAX_VALUE);
    }

    // PURPOSE: Invalid argument (0 threads) is rejected with IllegalArgumentException.
    @Test
    void rejectsBadThreadCount() {
        assertThatThrownBy(() -> ParallelSum.sum(new int[] {1}, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
