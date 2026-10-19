package prep.concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class ParallelSumTest {
    @Test
    void matchesSequentialSum() throws Exception {
        int[] data = IntStream.rangeClosed(1, 1_000_001).toArray();
        long expected = IntStream.of(data).asLongStream().sum();
        for (int threads : new int[] {1, 2, 3, 7, 16}) {
            assertThat(ParallelSum.sum(data, threads)).isEqualTo(expected);
        }
    }

    @Test
    void emptyAndTiny() throws Exception {
        assertThat(ParallelSum.sum(new int[] {}, 4)).isEqualTo(0L);
        assertThat(ParallelSum.sum(new int[] {5}, 8)).isEqualTo(5L);
    }

    @Test
    void largeValuesDoNotOverflowInt() throws Exception {
        int[] data = new int[10];
        java.util.Arrays.fill(data, Integer.MAX_VALUE);
        assertThat(ParallelSum.sum(data, 3)).isEqualTo(10L * Integer.MAX_VALUE);
    }

    @Test
    void rejectsBadThreadCount() {
        assertThatThrownBy(() -> ParallelSum.sum(new int[] {1}, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
