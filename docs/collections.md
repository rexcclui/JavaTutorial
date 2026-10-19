# Collections

Exercises: `LruCache`, `CollectionPuzzles` · Tests: `LruCacheTest`, `CollectionPuzzlesTest`
Run: `./run.sh collections`

## Key concepts / complexity
| Structure | get/contains | insert | notes |
|---|---|---|---|
| `ArrayList` | O(1) by index | O(1) amortized end, O(n) middle | contiguous, cache-friendly, default choice |
| `LinkedList` | O(n) | O(1) at known node | rarely wins in practice |
| `HashMap` | O(1) avg | O(1) avg | buckets; treeifies at 8 collisions (Java 8+) |
| `LinkedHashMap` | O(1) | O(1) | insertion/access order; `accessOrder=true` -> LRU |
| `TreeMap` | O(log n) | O(log n) | sorted, red-black tree, `floorKey/ceilingKey` |
| `PriorityQueue` | O(1) peek | O(log n) | binary min-heap; iteration order is NOT sorted |
| `ArrayDeque` | | O(1) both ends | prefer over `Stack`/`LinkedList` for stack & queue |

- **HashMap internals**: `hash(key)` spreads high bits, index = `hash & (n-1)`, resize doubles at load factor 0.75, collisions chain (list -> tree).
- **Fail-fast iterators** throw `ConcurrentModificationException` on structural modification; use `Iterator.remove()` or `removeIf`.
- `List.of(...)`/`Map.of(...)` are immutable (and reject nulls); `Arrays.asList` is fixed-size but writable-through.
- `Comparable` (natural order, one) vs `Comparator` (external, many).

## Common interview questions
1. How does `HashMap` work? What happens on collision / resize? Why power-of-two capacity?
2. `HashMap` vs `Hashtable` vs `ConcurrentHashMap` vs `Collections.synchronizedMap`.
3. `HashSet` is backed by what? How does it detect duplicates?
4. Implement an LRU cache (this repo!). Why `HashMap` + doubly linked list?
5. `ArrayList` vs `LinkedList`, and when is each actually better?
6. `Comparable` vs `Comparator`; what breaks if `compareTo` is inconsistent with `equals` in a `TreeSet`?

## Gotchas
- Mutating a key's fields after putting it in a `HashMap`/`HashSet` loses the entry.
- `PriorityQueue.toString()` / iteration is heap order, not sorted order.
- `Collectors.toMap` throws on duplicate keys unless you pass a merge function.
- Sorting with `(a, b) -> a - b` overflows; use `Integer.compare`.

## Links
- Collections Framework overview: https://docs.oracle.com/en/java/javase/21/core/java-collections-framework.html
- Big-O cheat sheet: https://www.bigocheatsheet.com/
- LRU Cache: https://leetcode.com/problems/lru-cache/ · Two Sum: https://leetcode.com/problems/two-sum/ · Top K Frequent: https://leetcode.com/problems/top-k-frequent-elements/ · Longest Consecutive: https://leetcode.com/problems/longest-consecutive-sequence/
