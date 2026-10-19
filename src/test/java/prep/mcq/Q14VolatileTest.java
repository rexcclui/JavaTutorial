package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 14 - volatile
 *
 * What does declaring a field volatile guarantee?
 *
 *   A. Writes by one thread become visible to other threads that read the field (and reads/writes are not reordered around it).
 *   B. Compound operations such as count++ become atomic.
 *   C. Only one thread at a time can execute the code that uses the field (mutual exclusion).
 *   D. The object the field refers to is never garbage collected.
 */
class Q14VolatileTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'A';

    private static volatile boolean stop;
    private static volatile int counter;

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - a worker spins until another thread sets a volatile flag. Volatile guarantees the worker sees the change and stops.
    @Test
    void concept_volatileFlagStopsWorker() throws Exception {
        stop = false;
        Thread worker = new Thread(() -> {
            while (!stop) {
                Thread.onSpinWait();
            }
        });
        worker.start();
        Thread.sleep(50);
        stop = true;                    // write is visible to the worker because the field is volatile
        worker.join(2000);
        assertThat(worker.isAlive()).isFalse();
        // Without `volatile` the JIT may keep the flag in a register and the worker could loop forever. That is the visibility problem.
    }

    // PURPOSE: Experiment for option B - replay the three steps of count++ (read, add, write) of two threads by hand: an update is lost although the field is volatile.
    @Test
    void experimentB_volatileDoesNotMakeIncrementAtomic() {
        counter = 0;
        int thread1Read = counter;      // thread 1 reads 0
        int thread2Read = counter;      // thread 2 reads 0 (before thread 1 wrote)
        counter = thread1Read + 1;      // thread 1 writes 1
        counter = thread2Read + 1;      // thread 2 writes 1 -> one increment lost
        assertThat(counter).isEqualTo(1);   // two increments happened, the result is 1
        // Verdict: B is false. Use AtomicInteger, LongAdder or synchronized for atomic compound operations.
    }

    // PURPOSE: Experiment for option C - volatile takes no lock, so a thread using it does not hold any monitor; synchronized does.
    @Test
    void experimentC_noMutualExclusion() {
        Object lock = new Object();
        counter = 5;
        assertThat(Thread.holdsLock(lock)).isFalse();
        synchronized (lock) {
            assertThat(Thread.holdsLock(lock)).isTrue();    // synchronized = lock held = mutual exclusion
        }
        // Verdict: C is false (the interleaving in experimentB also shows two "threads" inside the read-modify-write at once).
    }

    // PURPOSE: Experiment for option D - volatile is only a field modifier about memory visibility; it has nothing to do with object lifetime or GC.
    @Test
    void experimentD_unrelatedToGarbageCollection() {
        assertThat(Modifier.fieldModifiers() & Modifier.VOLATILE).isNotZero();    // legal on fields
        assertThat(Modifier.methodModifiers() & Modifier.VOLATILE).isZero();      // not on methods
        assertThat(Modifier.classModifiers() & Modifier.VOLATILE).isZero();       // not on classes
        // Reachability (strong/weak references) decides GC, not volatile. Verdict: D is false.
    }
}
