package prep.algorithms;

import java.util.PriorityQueue;

public class Sorting {

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

    public static int[] quickSort(int[] a) {
        quickSort(a, 0, a.length - 1);
        return a;
    }

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
