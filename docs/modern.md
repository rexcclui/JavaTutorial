# Modern Java (8 -> 21)

Exercises: `StreamPractice`, `ShapeMath` · Tests: `StreamPracticeTest`, `ShapeMathTest`
Run: `./run.sh modern`

## Key concepts
- **Lambdas & functional interfaces**: `Function`, `Predicate`, `Consumer`, `Supplier`, `BiFunction`, `UnaryOperator`. A lambda targets any interface with exactly one abstract method. Method references: `String::length`, `this::foo`, `Person::new`.
- **Streams**: lazy pipeline = source -> intermediate ops (`filter`, `map`, `flatMap`, `sorted`, `distinct`) -> ONE terminal op (`collect`, `reduce`, `forEach`, `findFirst`). Nothing runs until the terminal op. A stream can be consumed once.
- **Collectors**: `toList`, `groupingBy` (+ downstream `counting`, `mapping`), `partitioningBy`, `joining`, `toMap` (merge function!).
- **Optional**: a return type, not a field or parameter. Prefer `map/flatMap/orElseGet` to `isPresent()+get()`.
- **Records (16)**: immutable data carrier; generates ctor, accessors, `equals/hashCode/toString`.
- **Sealed types (17)**: `sealed ... permits ...` restricts the hierarchy, enabling exhaustive `switch` without `default`.
- **Pattern matching**: `instanceof` patterns (16), `switch` patterns with guards `when` (21), record deconstruction patterns (21).
- Also: `var` (10), text blocks (15), switch expressions (14), virtual threads (21).

## Common interview questions
1. `map` vs `flatMap`? `findFirst` vs `findAny`? `reduce` vs `collect`?
2. Are streams lazy? Show with `peek`.
3. When is `parallelStream()` a bad idea? (small data, shared mutable state, blocking IO, common ForkJoinPool starvation)
4. What is an effectively-final variable and why do lambdas require it?
5. Record vs Lombok `@Value` vs a normal class?

## Gotchas
- Streams of primitives (`IntStream`) avoid boxing; `Stream<Integer>.sum()` doesn't exist.
- `Stream.toList()` (16+) returns an unmodifiable list; `Collectors.toList()` makes no promise.
- `Optional.of(null)` throws; use `ofNullable`.
- Side effects inside `map`/`filter` and mutating the source mid-stream are bugs waiting to happen.

## Links
- Streams package docs: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html
- JEP 395 Records: https://openjdk.org/jeps/395 · JEP 409 Sealed Classes: https://openjdk.org/jeps/409 · JEP 441 Pattern Matching for switch: https://openjdk.org/jeps/441
- Oracle tutorial, Lambda expressions: https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
