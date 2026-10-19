# Multiple-choice Java questions (tech-test practice)

Run: `./run.sh mcq` (all questions) or `./run.sh mcq Q03` (one question; use two digits, `Q1` would also match Q10-Q17).

Each question is one test class in `src/test/java/prep/mcq/`. The question and its four options are in the class comment.

## How to work through a question
1. Open the file (for example `Q03HashMapNullTest.java`) and read ONLY the question and the options.
2. Decide your answer first. Write down why.
3. Set `MY_ANSWER = 'A'` (or B, C, D) and run `./run.sh mcq Q03`.
   - `SKIP checkMyAnswer`: you have not answered yet.
   - `PASS checkMyAnswer`: correct.
   - `FAIL checkMyAnswer`: wrong. The message does not tell you the answer.
4. Read the `experimentA/B/C/D` tests. Each runs real code that gathers evidence about ONE option and ends with a `// Verdict:` comment. This is the "why" an exam would never show you.
5. Read the `concept_` tests for the idea in its simplest form, and the `contrast_` / `twist_` tests for the near-miss variants that exams love.
6. Change values in the experiments and predict the result before running (see the "YOUR EDGE CASES" idea in the README).

Run `./run.sh --verbose mcq Q03` to see anything the tests print.

## Question index

| # | Topic | Skill it tests |
|---|---|---|
| Q01 | `Collectors.teeing()` | Streams: two collectors, one pass |
| Q02 | `Optional.orElse` vs `orElseGet` | Eager vs lazy evaluation |
| Q03 | `null` in `HashMap` (and other maps) | Collections |
| Q04 | `Arrays.asList` | Fixed-size list, view of an array |
| Q05 | `String` immutability | Ignoring a return value |
| Q06 | `try` / `finally` and `return` | Control flow |
| Q07 | Stream laziness | Intermediate vs terminal operations |
| Q08 | `Thread.run()` vs `start()` | Concurrency basics |
| Q09 | `List.of` | Immutable collections, nulls |
| Q10 | `ConcurrentModificationException` | Safe removal while iterating |
| Q11 | Default-method diamond | Interfaces (uses the real compiler in the test) |
| Q12 | `==` on strings, compile-time constants | String pool |
| Q13 | `thenApply` vs `thenCompose` | CompletableFuture |
| Q14 | `volatile` | Java memory model |
| Q15 | Generics invariance | `List<Object>` vs `List<String>` (compiler in the test) |
| Q16 | Static method hiding | Overriding vs hiding |
| Q17 | `Set.of` with duplicates | Immutable collections |

## Tips for real multiple-choice tests
- Read ALL options before choosing. Distractors are usually a true fact attached to the wrong method (for example Q08 option B: `IllegalThreadStateException` is real, but it comes from calling `start()` twice).
- "Always", "never", "only" in an option are red flags (Q10 option B: "always works").
- Watch for options that swap two things (Q02 option A has eager and lazy reversed).
- Output questions: check whether the code even compiles, and whether a return value is ignored (Q05).
- Questions can contain small factual mistakes (Q01 says Java 11; `teeing` arrived in Java 12). Choose the best-fitting option and move on.

## Add your own question
1. Copy any `QNN...Test.java` as a template and rename it `Q18YourTopicTest.java` (class name must end with `Test`).
2. Put the question and options in the class comment, set `CORRECT`, leave `MY_ANSWER = '?'`.
3. Write one `experiment` test per option that PROVES or REFUTES it with running code (a counter, an exception, a thread name, a reflection check). `McqSupport.compileErrors(name, source)` compiles a code string, so you can prove "this does not compile".
4. Put a `// PURPOSE:` comment above every test so the report explains it.
5. Run `./run.sh mcq Q18`.
