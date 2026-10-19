# JVM and Memory

Exercises: `Team` (shallow vs deep copy) · Tests: `TeamTest`, `JvmDemosTest` (runnable demos)
Run: `./run.sh jvm`

## Key concepts
- **Runtime areas**: heap (objects; young = eden + survivors, old gen), thread stacks (frames, locals), metaspace (class metadata, replaced PermGen in 8), code cache (JIT output).
- **Class loading**: load -> link (verify, prepare, resolve) -> initialize. Parent delegation: bootstrap -> platform -> application. Static init runs once, lazily, thread-safely.
- **Object init order**: parent static -> child static -> parent instance init + ctor -> child instance init + ctor (see `JvmDemosTest`).
- **Garbage collection**: reachability from GC roots (not reference counting). Generational hypothesis. Collectors: Serial, Parallel, G1 (default since 9), ZGC / Shenandoah (low pause). Minor vs major/full GC; stop-the-world pauses.
- **References**: strong, soft (caches), weak (canonical maps), phantom (cleanup). `finalize()` is deprecated; use `Cleaner`/try-with-resources.
- **JIT**: interpreter -> C1 -> C2; hot-spot detection, inlining, escape analysis.
- **Memory leaks in Java** = unintended strong references: static collections, listeners never removed, `ThreadLocal` in pools, unclosed resources.

## Common interview questions
1. Stack vs heap? Where do primitives, objects, statics, and string literals live?
2. How does GC decide an object is dead? What are GC roots?
3. `OutOfMemoryError` flavours (heap, metaspace, "unable to create native thread") and how to diagnose (heap dump, `jmap`, `jcmd`, VisualVM).
4. `StackOverflowError` causes? How to tune `-Xss`?
5. Shallow vs deep copy; `clone()` pitfalls.
6. What do `-Xms`, `-Xmx`, `-XX:+UseG1GC`, `-Xlog:gc` do?

## Links
- JVM specification: https://docs.oracle.com/javase/specs/jvms/se21/html/index.html
- GC tuning guide: https://docs.oracle.com/en/java/javase/21/gctuning/
- Tooling: `jcmd`, `jstack`, `jmap`, `jfr` (Java Flight Recorder), VisualVM
