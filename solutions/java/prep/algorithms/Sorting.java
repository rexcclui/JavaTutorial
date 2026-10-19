package prep.algorithms;

import java.util.PriorityQueue;

/**
 * Hand-written sorting algorithms plus a selection problem (k-th largest).
 */
public class Sorting {

    /**
     * Returns a sorted copy using merge sort (divide and conquer, stable).
     *
     * How it works:
     *   1. If the array has 0 or 1 element it is already sorted.
     *   2. Split it into a left half and a right half and sort each one recursively.
     *   3. Merge: repeatedly take the smaller of the two front elements. Using <= when they are equal keeps equal items in their original order (stable).
     *   4. Copy whatever is left over from the half that is not finished.
     *
     * Example: {5, 2, 4, 1}  ->  {1, 2, 4, 5}
     *
     * Complexity: time O(n log n), space O(n)
     */
    public static int[] mergeSort(int[] a) {
        if (a.length <= 1) return a.clone();
        int mid = a.length / 2;
        int[] left = mergeSort(java.util.Arrays.copyOfRange(a, 0, mid));
        int[] right = mergeSort(java.util.Arrays.copyOfRange(a, mid, a.length));
        int[] out = new int[a.length];
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            out[k++] = left[i] <= right[j] ? left[i++] : right[j++];   // <= keeps it stable
        }
        while (i < left.length) out[k++] = left[i++];
        while (j < right.length) out[k++] = right[j++];
        return out;
    }

    /**
     * Sorts the array in place using quick sort and returns the same array.
     *
     * How it works:
     *   1. Delegates to the recursive helper over the whole range.
     *
     * Complexity: average O(n log n), worst case O(n^2), space O(log n) for recursion
     */
    public static int[] quickSort(int[] a) {
        quickSort(a, 0, a.length - 1);
        return a;
    }

    /**
     * Sorts the part of the array between lo and hi (inclusive).
     *
     * How it works:
     *   1. Pick the middle element as the pivot (middle avoids the worst case on already-sorted input).
     *   2. Partition: i walks right until it finds a value >= pivot, j walks left until it finds a value <= pivot; swap them and move on, until i passes j.
     *   3. Afterwards everything left of i is <= pivot and everything right of j is >= pivot.
     *   4. Recursively sort the left part [lo, j] and the right part [i, hi].
     *
     * Complexity: average O(n log n)
     */
    private static void quickSort(int[] a, int lo, int hi) {
        if (lo >= hi) return;
        int pivot = a[lo + (hi - lo) / 2];
        int i = lo, j = hi;
        while (i <= j) {                                // Hoare-style partition
            while (a[i] < pivot) i++;
            while (a[j] > pivot) j--;
            if (i <= j) {
                int t = a[i]; a[i] = a[j]; a[j] = t;
                i++; j--;
            }
        }
        quickSort(a, lo, j);
        quickSort(a, i, hi);
    }

    /**
     * The k-th largest value (k = 1 is the maximum) without sorting the whole array.
     *
     * How it works:
     *   1. Reject k outside 1..length.
     *   2. Keep a min-heap (PriorityQueue) that never holds more than k elements.
     *   3. Add every number; whenever the heap grows beyond k, remove the smallest.
     *   4. At the end the heap holds the k largest numbers, and its smallest element (the top) is the k-th largest.
     *
     * Example: {3, 2, 1, 5, 6, 4}, k=2  ->  5
     *
     * Complexity: time O(n log k), space O(k)
     */
    public static int kthLargest(int[] a, int k) {
        if (k < 1 || k > a.length) throw new IllegalArgumentException("k out of range");
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
        for (int n : a) {
            minHeap.offer(n);
            if (minHeap.size() > k) minHeap.poll();
        }
        return minHeap.peek();
        // TWIST: implement quickselect for O(n) average. What input makes it O(n^2)?
    }
}
