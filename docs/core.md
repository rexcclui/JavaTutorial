# Core Java

Exercises: `Point`, `StringPractice` · Tests: `PointTest`, `StringPracticeTest`, `PuzzlesTest` (predict-the-output)
Run: `./run.sh core`

## Key concepts
- **equals/hashCode contract**: reflexive, symmetric, transitive, consistent, `x.equals(null) == false`. Equal objects MUST have equal hash codes (the reverse isn't required). Break it and `HashMap`/`HashSet` silently misbehave.
- **Immutability**: `final` class, `final` fields, no setters, defensive copies of mutable inputs/outputs. Why `String` is immutable: string pool, hash caching, thread safety, security.
- **Pass-by-value**: Java always copies the *value* of the variable; for objects that value is a reference. You can mutate the object, you can't re-point the caller's variable.
- **String vs StringBuilder vs StringBuffer**: immutable / mutable unsynchronized / mutable synchronized. Concatenating in a loop with `+` is O(n²).
- **Integer cache**: `Integer.valueOf` caches -128..127, so `==` on boxed values is a trap. Always `equals`.
- **Checked vs unchecked exceptions**, `try-with-resources` (`AutoCloseable`), `finally` runs even after `return`, suppressed exceptions.
- **Overloading vs overriding**: overloads are resolved at compile time by static types; overrides at runtime.
- **abstract class vs interface**: state and constructors vs multiple inheritance of type; default/static/private interface methods since 8/9.

## Common interview questions
1. Why override `hashCode` when you override `equals`?
2. Is Java pass-by-value or pass-by-reference? Prove it with `swap(a, b)`.
3. Why is `String` immutable? What is `intern()`?
4. `final` vs `finally` vs `finalize`?
5. What happens if a `HashMap` key is mutated after insertion?
6. What does `static` mean on a field, method, nested class, and initializer block?

## Gotchas
- `Math.abs(Integer.MIN_VALUE)` is negative. `Integer.MAX_VALUE + 1` wraps.
- `0.1 + 0.2 != 0.3`; use `BigDecimal` (constructed from strings) for money.
- `list.remove(int)` vs `list.remove(Object)` on `List<Integer>`.
- `char + int` is `int`; `c += 1` compiles because compound assignment casts.

## Links (not verified from the build sandbox; open them in your browser)
- Oracle Java Tutorials: https://docs.oracle.com/javase/tutorial/
- Java 21 API docs: https://docs.oracle.com/en/java/javase/21/docs/api/index.html
- Baeldung equals/hashCode: https://www.baeldung.com/java-equals-hashcode-contracts
- Book: *Effective Java* (Bloch), items on equals/hashCode, immutability, try-with-resources
- Palindrome: https://leetcode.com/problems/valid-palindrome/ · Anagram: https://leetcode.com/problems/valid-anagram/
