# Data Structures

Exercises: `LinkedListOps`, `MinStack`, `TreeOps`, `GraphOps` · Tests: matching `*Test` classes
Run: `./run.sh datastructures`

## Key concepts
- **Linked list**: dummy-head trick removes edge cases; slow/fast pointers find the middle and cycles; reverse = save next, flip, advance.
- **Stack / queue**: use `ArrayDeque` (`push/pop/peek`, `offer/poll`). `Stack` is legacy. Monotonic stack for "next greater element".
- **Binary tree**: DFS (pre/in/post) recursively or with an explicit stack; BFS with a queue and per-level loop. BST in-order is sorted. Validate with *bounds*, not just parent comparison.
- **Heap**: `PriorityQueue` (min-heap by default; `Collections.reverseOrder()` for max).
- **Trie**: prefix tree for autocomplete/word search. **Union-Find** for connectivity/components.
- **Graph**: adjacency list `Map<Integer, List<Integer>>`; BFS = shortest path in unweighted graph; DFS = components/cycles; Kahn's algorithm = topological order; Dijkstra for weighted (non-negative).

## Common interview questions
1. Reverse a linked list (iteratively, then recursively). Detect and find the start of a cycle.
2. Validate a BST. Find the LCA. Level-order traversal. Serialize/deserialize a tree.
3. Min stack / queue using two stacks.
4. Number of islands, clone graph, course schedule.
5. BFS vs DFS: when would you choose each, and what are their space costs?

## Gotchas
- Mark nodes visited when you *enqueue* (BFS) to avoid duplicates in the queue.
- Deep recursion on skewed trees / big grids can `StackOverflowError`; use an explicit stack.
- `new int[]{...}` keys don't work in sets (identity hash); wrap in a record or encode as a string/long.

## Links
- Linked list: https://leetcode.com/problems/reverse-linked-list/ · https://leetcode.com/problems/linked-list-cycle/
- Trees: https://leetcode.com/problems/validate-binary-search-tree/ · https://leetcode.com/problems/binary-tree-level-order-traversal/ · https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/
- Graphs: https://leetcode.com/problems/number-of-islands/ · https://leetcode.com/problems/course-schedule/
- Min stack: https://leetcode.com/problems/min-stack/
- Visualiser: https://visualgo.net/en
