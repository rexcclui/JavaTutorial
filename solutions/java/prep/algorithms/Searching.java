package prep.algorithms;

/**
 * Binary search and two common variations. All need sorted input.
 */
public class Searching {

    /**
     * Index of the target in a sorted array, or -1 if it is not present.
     *
     * How it works:
     *   1. Keep a search window [lo, hi] covering the whole array.
     *   2. Look at the middle element: equal means found; smaller than target means the answer must be to the right (lo = mid + 1); otherwise to the left (hi = mid - 1).
     *   3. Each step halves the window. If the window becomes empty the value is not there.
     *   4. mid = lo + (hi - lo) / 2 instead of (lo + hi) / 2 avoids integer overflow.
     *
     * Example: {1, 3, 5, 7}, 5  ->  2
     *
     * Complexity: time O(log n), space O(1)
     */
    public static int binarySearch(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;          // (lo + hi) / 2 can overflow
            if (a[mid] == target) return mid;
            if (a[mid] < target) lo = mid + 1; else hi = mid - 1;
        }
        return -1;
    }

    /**
     * Index of the first element that is >= target (a.length if every element is smaller). Handy for inserting into a sorted array.
     *
     * How it works:
     *   1. Use a half-open window [lo, hi) with hi = length.
     *   2. If a[mid] < target the answer is after mid (lo = mid + 1); otherwise mid could be the answer, so keep it (hi = mid).
     *   3. When lo meets hi, that is the first position that is not smaller than target.
     *
     * Example: {1, 2, 2, 2, 5}, 2  ->  1
     *
     * Complexity: time O(log n), space O(1)
     */
    public static int lowerBound(int[] a, int target) {
        int lo = 0, hi = a.length;                 // half-open [lo, hi)
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < target) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    /**
     * Binary search in a sorted array that was rotated at an unknown point, such as {4,5,6,7,0,1,2}.
     *
     * How it works:
     *   1. Key idea: after splitting at mid, at least one half is still sorted.
     *   2. If a[lo] <= a[mid], the left half is sorted: if the target lies inside it go left, else go right.
     *   3. Otherwise the right half is sorted: if the target lies inside it go right, else go left.
     *   4. Repeat; return the index when found, -1 if the window empties.
     *
     * Example: {4,5,6,7,0,1,2}, 0  ->  4
     *
     * Complexity: time O(log n), space O(1)
     */
    public static int searchRotated(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == target) return mid;
            if (a[lo] <= a[mid]) {                 // left half is sorted
                if (a[lo] <= target && target < a[mid]) hi = mid - 1; else lo = mid + 1;
            } else {                               // right half is sorted
                if (a[mid] < target && target <= a[hi]) lo = mid + 1; else hi = mid - 1;
            }
        }
        return -1;
    }
}
