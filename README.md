# Java Interview Prep

A hands-on repo to practise Java for interviews. Every topic has a **guide**, **links**, **exercises with tests**, a **reference solution**, and **"twist" ideas** to test your own understanding.

## Quick start

Requirements: JDK 21 and (optionally) Maven. The Maven Wrapper (`./mvnw`) is included, so only a JDK is strictly needed.

```bash
./run.sh --list              # list categories
./run.sh                     # run everything (unsolved exercises fail, that's the point)
./run.sh collections         # run one category
./run.sh core PointTest      # run one test class
./run.sh --solutions         # run against reference solutions (sanity check)
mvn test -Dtest=PointTest#toStringFormat    # one test method
```

IDE: open `pom.xml` in IntelliJ / VS Code; every test has a run/debug button.

## How it works

```
exercises/java/prep/<category>/   <- YOUR CODE: stubs that throw UnsupportedOperationException("TODO")
solutions/java/prep/<category>/   <- reference solutions (same class names; peek after you try)
src/test/java/prep/<category>/    <- tests (same tests verify both)
docs/<category>.md                <- guide: concepts, common questions, gotchas, links
```

1. Read `docs/<category>.md`.
2. Open an exercise in `exercises/`, read the Javadoc, implement it.
3. `./run.sh <category>` until green.
4. Compare with `solutions/`. Read the `// TWIST:` comments there.

`-Dsolutions` just swaps the compiled source folder, so the same tests run against the reference code.

## Test your own learning (twist the code)

- **Break it on purpose**: follow the `// TWIST:` comments in `solutions/`. Predict what fails, run, compare. (e.g. delete `hashCode()` from `Point`.)
- **Predict the output**: `src/test/java/prep/core/PuzzlesTest.java` and `jvm/JvmDemosTest.java`. Cover the assertion, guess, then check. Change an expected value to see the failure message.
- **Raise the level**: tests marked `@Disabled("Level 2 ...")` are harder variants. Remove `@Disabled` after you extend the code.
- **Add your own edge cases** under `// YOUR EDGE CASES HERE` in each test (null, empty, huge, duplicates, negatives).
- **Explain it back**: write time/space complexity and your reasoning in a comment above each solution, then compare with the guide.
- **Use git as a safety net**: `git switch -c practice/<topic>`; `git diff` shows your twist, `git restore .` resets it.

## Categories and progress

Tick them off as you go.

| Done | Category | Guide | Exercises |
|---|---|---|---|
| [ ] | Core Java | [docs/core.md](docs/core.md) | `Point`, `StringPractice`, puzzles |
| [ ] | Collections | [docs/collections.md](docs/collections.md) | `LruCache`, `CollectionPuzzles` |
| [ ] | Modern Java 8-21 | [docs/modern.md](docs/modern.md) | `StreamPractice`, `ShapeMath` |
| [ ] | Concurrency | [docs/concurrency.md](docs/concurrency.md) | `SafeCounter`, `BoundedBuffer`, `ParallelSum` |
| [ ] | Algorithms | [docs/algorithms.md](docs/algorithms.md) | `Searching`, `Sorting`, `ArrayTricks` |
| [ ] | Data structures | [docs/datastructures.md](docs/datastructures.md) | `LinkedListOps`, `MinStack`, `TreeOps`, `GraphOps` |
| [ ] | Design patterns | [docs/patterns.md](docs/patterns.md) | `AppConfig`, `Pizza`, `EventBus` |
| [ ] | Low-level design | [docs/design.md](docs/design.md) | `RateLimiter`, `ParkingLot` |
| [ ] | JVM and memory | [docs/jvm.md](docs/jvm.md) | `Team`, demos |

Suggested order: core -> collections -> algorithms -> data structures -> modern -> concurrency -> patterns -> design -> jvm.

## Adding your own exercise

1. Add a stub in `exercises/java/prep/<category>/Foo.java` and the working version in `solutions/java/prep/<category>/Foo.java` (same package and class name, same public API).
2. Add `src/test/java/prep/<category>/FooTest.java`.
3. Check both modes: `./run.sh --solutions <category>` must be green; `./run.sh <category>` should fail until solved.

## Notes

- Links in the guides point at stable, well-known sites (Oracle docs, JEPs, LeetCode, Refactoring Guru, etc.). They were written without network verification, so report any that moved.
- Concurrency tests use timeouts and repeated runs; run them a few times after you think you're done.
