# Concurrency

Exercises: `SafeCounter`, `BoundedBuffer`, `ParallelSum` · Tests: matching `*Test` classes
Run: `./run.sh concurrency` (stubs here fail for real races, so rerun a few times after fixing)

## Key concepts
- **Visibility, atomicity, ordering** are the three problems. `synchronized`, `volatile`, `Lock`, and atomics address them differently.
- **`volatile`**: visibility + ordering, NOT atomicity (`count++` on a volatile is still a race).
- **`synchronized`**: mutual exclusion + happens-before on the same monitor. `wait()/notify()` must hold the monitor and be called in a `while` loop (spurious wakeups, multiple waiters).
- **java.util.concurrent**: `ExecutorService`, `Future`, `CompletableFuture`, `ConcurrentHashMap`, `CountDownLatch`, `Semaphore`, `BlockingQueue`, `ReentrantLock`/`Condition`, `AtomicInteger`/`LongAdder`.
- **Thread pools**: fixed vs cached vs scheduled; bounded queues and rejection policies; always `shutdown()`.
- **Deadlock** needs mutual exclusion + hold-and-wait + no preemption + circular wait. Fix by lock ordering or `tryLock` with timeout.
- **Java Memory Model**: happens-before edges (monitor unlock->lock, volatile write->read, `Thread.start/join`).
- **Virtual threads (21)**: cheap threads for blocking IO; don't pool them, avoid pinning with `synchronized` around blocking calls.

## Common interview questions
1. `synchronized` vs `ReentrantLock`? `volatile` vs `AtomicInteger`?
2. How would you implement a blocking queue? (this repo's `BoundedBuffer`)
3. How does `ConcurrentHashMap` stay thread-safe without a global lock?
4. What is a race condition vs a data race vs a deadlock vs livelock vs starvation?
5. `Runnable` vs `Callable`; `submit` vs `execute`; what happens to exceptions in each?
6. How do you stop a thread? (interruption is cooperative; never `Thread.stop`)
7. Explain double-checked locking and why the field must be `volatile`.

## Gotchas
- Swallowing `InterruptedException` -> restore with `Thread.currentThread().interrupt()`.
- `Executors.newFixedThreadPool` uses an unbounded queue -> memory risk. `newCachedThreadPool` can create unbounded threads.
- `parallelStream` uses the shared common pool.
- `SimpleDateFormat` is not thread-safe; use `java.time`.

## Links
- Oracle concurrency tutorial: https://docs.oracle.com/javase/tutorial/essential/concurrency/
- Jenkov Java concurrency tutorial: https://jenkov.com/tutorials/java-concurrency/index.html
- Book: *Java Concurrency in Practice* (Goetz et al.)
- JEP 444 Virtual Threads: https://openjdk.org/jeps/444
