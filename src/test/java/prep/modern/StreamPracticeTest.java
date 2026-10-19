package prep.modern;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class StreamPracticeTest {
    @Test
    void evenSquares() {
        assertThat(StreamPractice.evenSquares(List.of(1, 2, 3, 4))).containsExactly(4, 16);
        assertThat(StreamPractice.evenSquares(List.of())).isEmpty();
    }

    @Test
    void groupByLength() {
        var result = StreamPractice.groupByLength(List.of("a", "bb", "cc", "d"));
        assertThat(result).containsEntry(1, List.of("a", "d")).containsEntry(2, List.of("bb", "cc"));
    }

    @Test
    void longest() {
        assertThat(StreamPractice.longest(List.of("aa", "bbb", "ccc"))).isEqualTo(Optional.of("bbb"));
        assertThat(StreamPractice.longest(List.of())).isEmpty();
    }

    @Test
    void joinUpper() {
        assertThat(StreamPractice.joinUpper(List.of("a", "b"))).isEqualTo("A, B");
    }

    @Test
    void average() {
        assertThat(StreamPractice.average(List.of(1, 2, 3, 4))).isEqualTo(2.5);
        assertThat(StreamPractice.average(List.of())).isEqualTo(0.0);
    }

    @Test
    void flattenDistinctSorted() {
        assertThat(StreamPractice.flattenDistinctSorted(List.of(List.of(3, 1), List.of(2, 3), List.of())))
                .containsExactly(1, 2, 3);
    }

    // YOUR EDGE CASES HERE
}
