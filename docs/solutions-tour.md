# Solutions tour (read-only learning)

Run `./run.sh --learn` (or `./run.sh --learn <category>`) to execute every reference solution and list what each one is verified to do. Then read the files below in this order. Each entry says what to notice. The tests in `src/test/java/prep/<category>/` show the behaviour in concrete examples, so read a test next to its solution.

Paths are under `solutions/java/prep/`.

## 1. core
- `core/Point.java`: equals + hashCode + toString. Notice `instanceof` pattern, `Objects.hash`, final class and fields. Test: `PointTest` (HashSet and HashMap use).
- `core/StringPractice.java`: two pointers (palindrome), `LinkedHashMap` for "first non-repeated" (insertion order), sorting chars for anagram.
- Test only: `PuzzlesTest`: Integer cache, string pool, pass-by-value, `finally` overriding `return`, overflow, floating point. Read the comments next to each assertion.

## 2. collections
- `collections/LruCache.java`: HashMap + doubly linked list with sentinel head/tail. Notice every operation is O(1) and why the map stores nodes, not values.
- `collections/CollectionPuzzles.java`: `twoSum` (complement map), `topKWords` (group + sort with comparator chain), `longestConsecutive` (start only at run beginnings, so O(n)).

## 3. algorithms
- `algorithms/Searching.java`: `lo + (hi - lo) / 2`; closed vs half-open ranges; rotated search = "one half is always sorted".
- `algorithms/Sorting.java`: merge sort (stable, `<=`), quicksort (Hoare partition), k-th largest with a size-k min-heap.
- `algorithms/ArrayTricks.java`: sliding window (last-seen index), Kadane, iterative Fibonacci, coin-change DP table.

## 4. datastructures
- `datastructures/LinkedListOps.java`: reverse (prev/cur/next), fast/slow pointers, dummy head merge.
- `datastructures/MinStack.java`: second stack holds running minimum.
- `datastructures/TreeOps.java`: iterative in-order with explicit stack, BFS by level size, BST validation with bounds (not just parent compare).
- `datastructures/GraphOps.java`: BFS marks visited on enqueue, DFS, shortest path by levels, islands with explicit stack, Kahn's topological sort.

## 5. modern (Java 8-21)
- `modern/StreamPractice.java`: filter/map/groupingBy/joining/flatMap/distinct. Notice `reduce` used so the first of equal-length strings wins.
- `modern/ShapeMath.java`: sealed interface + records + `switch` pattern matching with a `when` guard and no `default`.

## 6. concurrency
- `concurrency/SafeCounter.java`: AtomicInteger vs the racy `count++`.
- `concurrency/BoundedBuffer.java`: `synchronized` + `wait()` in a `while` loop + `notifyAll()`.
- `concurrency/ParallelSum.java`: executor, Callable per chunk, Futures, `shutdown()` in `finally`, long accumulator to avoid overflow.

## 7. patterns
- `patterns/AppConfig.java`: holder-idiom singleton.
- `patterns/Pizza.java`: builder with validation in `build()` and a defensive copy (`List.copyOf`).
- `patterns/EventBus.java`: observer with `CopyOnWriteArrayList` so unsubscribing during publish is safe; listener exceptions are isolated.

## 8. design
- `design/RateLimiter.java`: token bucket, refill computed lazily from an injected clock, `synchronized`.
- `design/ParkingLot.java`: `TreeSet` for lowest free slot + `HashMap` plate -> slot.

## 9. jvm
- `jvm/Team.java`: deep copy vs shallow copy.
- Test only: `JvmDemosTest`: StackOverflowError is an Error, class init order, weak references, boxed `equals` pitfalls.

## Using your own twists
Every solution has `// TWIST:` comments. Copy the file into `exercises/java/prep/<category>/`, change it, and run `./run.sh <category>` to see your change tested.
