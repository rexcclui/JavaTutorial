# Low-Level Design

Exercises: `RateLimiter` (token bucket), `ParkingLot` · Tests: matching `*Test` classes
Run: `./run.sh design`

## Framework for an LLD round (~35 min)
1. **Clarify requirements** and list use cases; agree what's out of scope.
2. **Identify entities and relationships** (nouns -> classes, verbs -> methods). Draw it.
3. **Define the public API first**, then fields. Keep classes small and cohesive.
4. **Pick data structures for the hot path** and state complexity (e.g. `TreeSet` for lowest free slot).
5. **Handle concurrency and failure**: what is shared, what lock, what exceptions.
6. **Make time and randomness injectable** (`LongSupplier` clock) so it is testable.
7. **Extension points**: where would new requirements slot in (Strategy, enum, new subclass)?

## Classic LLD problems to practise next (build them yourself in `exercises/`)
LRU cache (done) · rate limiter variants (fixed window, sliding log, sliding counter) · elevator · tic-tac-toe / chess · vending machine · URL shortener · logger framework · in-memory file system · pub/sub · task scheduler · connection pool

## Common interview questions
1. Token bucket vs leaky bucket vs sliding window: trade-offs?
2. How does your design change distributed (Redis, per-user keys, clock skew)?
3. How do you keep it thread-safe without a global lock?

## Links
- System design primer: https://github.com/donnemartin/system-design-primer
- Refactoring Guru patterns: https://refactoring.guru/design-patterns/java
