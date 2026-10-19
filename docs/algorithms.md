# Algorithms

Exercises: `Searching`, `Sorting`, `ArrayTricks` · Tests: matching `*Test` classes
Run: `./run.sh algorithms`

## Patterns to recognise
| Signal in the problem | Pattern | Example here |
|---|---|---|
| sorted input, "find" | binary search (also on the answer space) | `Searching` |
| contiguous subarray/substring, max/min length or sum | sliding window | `longestUniqueSubstring`, `maxSumWindow` |
| pair/triple in sorted array, palindrome | two pointers | `StringPractice.isPalindrome` |
| "have I seen this?" / complement lookup | hash set / map | `CollectionPuzzles.twoSum` |
| overlapping subproblems, "min/max/count ways" | dynamic programming | `fib`, `coinChange`, `maxSubarray` |
| top-k, streaming median | heap / priority queue | `Sorting.kthLargest` |
| ordering with dependencies | topological sort | `GraphOps.canFinish` |

## Complexity cheat sheet
- Sorting: merge O(n log n) stable, extra O(n); quick O(n log n) avg / O(n²) worst, in place; heap O(n log n) in place; `Arrays.sort(int[])` is dual-pivot quicksort, `Arrays.sort(Object[])` is TimSort (stable).
- Binary search O(log n). Hash lookup O(1) avg. Heap push/pop O(log n).

## How to attack an interview problem
1. Restate and ask about input size, duplicates, negatives, empty input, overflow.
2. Brute force first, state its complexity, then optimise.
3. Walk through a small example by hand before coding.
4. Code, then test with edge cases (empty, one element, all same, max values).
5. State final time and space complexity.

## Gotchas
- `mid = (lo + hi) / 2` overflows; use `lo + (hi - lo) / 2`.
- Off-by-one: decide closed `[lo, hi]` vs half-open `[lo, hi)` and stay consistent.
- Don't compare strings/boxed values with `==`.

## Links
- NeetCode roadmap: https://neetcode.io/roadmap
- Big-O cheat sheet: https://www.bigocheatsheet.com/
- Problems: https://leetcode.com/problems/binary-search/ · https://leetcode.com/problems/search-in-rotated-sorted-array/ · https://leetcode.com/problems/sort-an-array/ · https://leetcode.com/problems/kth-largest-element-in-an-array/ · https://leetcode.com/problems/longest-substring-without-repeating-characters/ · https://leetcode.com/problems/maximum-subarray/ · https://leetcode.com/problems/coin-change/
