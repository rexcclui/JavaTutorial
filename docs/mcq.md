# Multiple-choice Java questions (tech-test practice)

35 questions, each with the answer stated up front and runnable code that lets you SEE why.

```bash
./run.sh mcq            # all questions: one summary line per question
./run.sh mcq Q12        # one question: every test with its purpose and what it printed
```

(Use two digits: `Q1` also matches Q10-Q19.)

## What is in each question file (`src/test/java/prep/mcq/QNN...Test.java`)
1. **The question and options A-D** in the class comment.
2. **`ANSWER: X`** and **`WHY:`** a line for every option saying why it is right or wrong.
3. **`demo_` tests** - the idea in its simplest form. They print a trace (counters, thread names, object identities, compiler messages), and `run.sh` shows that output under the test.
4. **`optionX_wrong_` / `optionX_correct_` tests** - one per option, each proving the verdict with running code. Some use the real Java compiler on a code string to prove "this does not compile" (`McqSupport.compileErrors`).
5. **`contrast_` / `twist_` / `fix_` tests** - the near-miss variants that exams love.

Read the class comment first, run the question, read the printed output, then open the test code to see how the output was produced. Change a value and rerun to test your own understanding.

## Question index

| # | Topic | Answer |
|---|---|---|
| Q01 | `Collectors.teeing()` | B |
| Q02 | `Optional.orElse` vs `orElseGet` | C |
| Q03 | `null` in `HashMap` and other maps | A |
| Q04 | `Arrays.asList` is fixed-size | D |
| Q05 | `String` immutability, ignored return value | A |
| Q06 | `try` / `finally` and `return` | C |
| Q07 | Stream laziness | B |
| Q08 | `Thread.run()` vs `start()` | D |
| Q09 | `List.of` is immutable | C |
| Q10 | Removing while iterating (`ConcurrentModificationException`) | A |
| Q11 | Default-method diamond | B |
| Q12 | `==` on strings and compile-time constants | C |
| Q13 | `thenApply` vs `thenCompose` | D |
| Q14 | `volatile` | A |
| Q15 | Generics invariance | B |
| Q16 | Static method hiding | D |
| Q17 | `Set.of` with duplicates | C |
| Q18 | `equals` without `hashCode` | D |
| Q19 | `Integer` cache, `==` | B |
| Q20 | `List<Integer>.remove(int)` vs `remove(Object)` | C |
| Q21 | Pass-by-value | A |
| Q22 | `final` reference vs immutable object | D |
| Q23 | Interface fields are `public static final` | B |
| Q24 | Checked vs unchecked exceptions | C |
| Q25 | try-with-resources order and suppressed exceptions | A |
| Q26 | Map iteration order | D |
| Q27 | `TreeSet` without `Comparable` | B |
| Q28 | Records | C |
| Q29 | `map` vs `flatMap` | A |
| Q30 | Exceptions from `Future.get()` | D |
| Q31 | `synchronized` static vs instance | B |
| Q32 | When static initializers run | C |
| Q33 | Auto-unboxing `null` | A |
| Q34 | `byte` arithmetic and compound assignment | D |
| Q35 | Overload resolution with `null` | B |

## Tips for real multiple-choice tests
- Read ALL options before choosing. Distractors are usually a true fact attached to the wrong method (Q08 option B: `IllegalThreadStateException` is real, but it comes from calling `start()` twice).
- "Always", "never", "only" in an option are red flags (Q10 option B: "always works").
- Watch for options that swap two things (Q02 option A has eager and lazy reversed).
- Output questions: first check whether the code compiles, and whether a return value is ignored (Q05), a reference is compared with `==` (Q12, Q19), or an overload is chosen at compile time (Q35).
- Questions can contain small factual mistakes (Q01 says Java 11; `teeing` arrived in Java 12). Choose the best-fitting option and move on.

## Add your own question
1. Copy any `QNN...Test.java` and rename it `Q36YourTopicTest.java` (the class name must end with `Test`).
2. Write the question, options, `ANSWER:` and `WHY:` in the class comment.
3. Add a `demo_` test that prints a trace with `McqSupport.show(...)`, and one test per option that proves it with code.
   - `McqSupport.attempt("label", () -> ...)` prints "OK" or the exception name.
   - `McqSupport.compileErrors("Snippet", source)` returns the compiler's error messages (empty = compiles).
4. Put a `// PURPOSE:` comment above every test.
5. Run `./run.sh mcq Q36`.
