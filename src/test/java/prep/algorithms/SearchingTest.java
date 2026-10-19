package prep.algorithms;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SearchingTest {
    // PURPOSE: Finds every existing element's index, returns -1 for missing values and for an empty array.
    @Test
    void binarySearch() {
        int[] a = {1, 3, 5, 7, 9, 11};
        for (int i = 0; i < a.length; i++) assertThat(Searching.binarySearch(a, a[i])).isEqualTo(i);
        assertThat(Searching.binarySearch(a, 4)).isEqualTo(-1);
        assertThat(Searching.binarySearch(new int[] {}, 4)).isEqualTo(-1);
    }

    // PURPOSE: Index of the first element >= target, including before-first, between values, duplicates and past-the-end.
    @Test
    void lowerBound() {
        int[] a = {1, 2, 2, 2, 5};
        assertThat(Searching.lowerBound(a, 2)).isEqualTo(1);
        assertThat(Searching.lowerBound(a, 0)).isEqualTo(0);
        assertThat(Searching.lowerBound(a, 3)).isEqualTo(4);
        assertThat(Searching.lowerBound(a, 9)).isEqualTo(5);
    }

    // PURPOSE: Binary search on a rotated sorted array: find every element and reject missing ones.
    @Test
    void searchRotated() {
        int[] a = {4, 5, 6, 7, 0, 1, 2};
        for (int i = 0; i < a.length; i++) assertThat(Searching.searchRotated(a, a[i])).isEqualTo(i);
        assertThat(Searching.searchRotated(a, 3)).isEqualTo(-1);
        assertThat(Searching.searchRotated(new int[] {1}, 1)).isEqualTo(0);
    }

    // PURPOSE: Reminder that mid = (lo + hi) / 2 can overflow; use lo + (hi - lo) / 2.
    @Test
    void hugeIndicesDoNotOverflow() {
        // mid = (lo + hi) / 2 overflows for arrays near Integer.MAX_VALUE; the formula matters even if this test can't allocate it.
        assertThat(Searching.binarySearch(new int[] {Integer.MAX_VALUE}, Integer.MAX_VALUE)).isEqualTo(0);
    }
}
