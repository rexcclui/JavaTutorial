package prep.algorithms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SortingTest {
    private static int[] sample() {
        int[] a = new int[500];
        Random r = new Random(42);
        for (int i = 0; i < a.length; i++) a[i] = r.nextInt(100) - 50;   // duplicates + negatives
        return a;
    }

    @Test
    void mergeSortMatchesJdk() {
        int[] a = sample(), expected = a.clone();
        Arrays.sort(expected);
        assertThat(Sorting.mergeSort(a)).containsExactly(expected);
    }

    @Test
    void quickSortMatchesJdk() {
        int[] a = sample(), expected = a.clone();
        Arrays.sort(expected);
        assertThat(Sorting.quickSort(a)).containsExactly(expected);
    }

    @Test
    void edgeCases() {
        assertThat(Sorting.mergeSort(new int[] {})).isEmpty();
        assertThat(Sorting.quickSort(new int[] {1})).containsExactly(1);
        assertThat(Sorting.quickSort(new int[] {3, 3, 3})).containsExactly(3, 3, 3);
        assertThat(Sorting.mergeSort(new int[] {5, 4, 3, 2, 1})).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void kthLargest() {
        assertThat(Sorting.kthLargest(new int[] {3, 2, 1, 5, 6, 4}, 2)).isEqualTo(5);
        assertThat(Sorting.kthLargest(new int[] {3, 2, 3, 1, 2, 4, 5, 5, 6}, 4)).isEqualTo(4);
        assertThatThrownBy(() -> Sorting.kthLargest(new int[] {1}, 2)).isInstanceOf(IllegalArgumentException.class);
    }
}
