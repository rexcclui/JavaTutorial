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
 *
 * ANSWER: A
 *
 * WHY:
 *   A. Correct - visibility and ordering.
 *   B. Wrong - count++ is read, add, write: three steps. Two threads can interleave and one update is lost. Use AtomicInteger / LongAdder / synchronized.
 *   C. Wrong - volatile takes no lock. synchronized (or a Lock) gives mutual exclusion.
 *   D. Wrong - garbage collection depends on reachability, not on volatile.
 *
 * See it run:  ./run.sh mcq Q14
 */
class Q14VolatileTest {
    private static volatile boolean stop;
    private static volatile int counter;

    // PURPOSE: Demo - a worker spins until another thread sets a volatile flag; volatile guarantees the worker sees the change and stops.
    @Test
    void demo_volatileFlagStopsWorker() throws Exception {
        stop = false;
        Thread worker = new Thread(() -> {
            while (!stop) {
                Thread.onSpinWait();
            }
        });
        worker.start();
        Thread.sleep(50);
        McqSupport.show("main: setting stop = true (volatile write)");
        stop = true;
        worker.join(2000);
        McqSupport.show("worker still alive after 2s? %s   <- it saw the write and finished", worker.isAlive());
        assertThat(worker.isAlive()).isFalse();
        // Without `volatile` the JIT may keep the flag in a register and the worker could loop forever: the visibility problem.
    }

    // PURPOSE: Option B is WRONG - replay count++ of two threads by hand: both read 0, both write 1, one increment is lost although the field is volatile.
    @Test
    void optionB_wrong_volatileDoesNotMakeIncrementAtomic() {
        counter = 0;
        int thread1Read = counter;
        int thread2Read = counter;
        McqSupport.show("thread 1 reads counter -> %d", thread1Read);
        McqSupport.show("thread 2 reads counter -> %d   (before thread 1 wrote!)", thread2Read);
        counter = thread1Read + 1;
        McqSupport.show("thread 1 writes %d", thread1Read + 1);
        counter = thread2Read + 1;
        McqSupport.show("thread 2 writes %d", thread2Read + 1);
        McqSupport.show("final counter = %d   (two increments happened, one was lost)", counter);
        assertThat(counter).isEqualTo(1);
    }

    // PURPOSE: Option C is WRONG - volatile takes no lock; synchronized does (Thread.holdsLock tells them apart).
    @Test
    void optionC_wrong_noMutualExclusion() {
        Object lock = new Object();
        counter = 5;
        McqSupport.show("after a volatile write      : holdsLock = %s", Thread.holdsLock(lock));
        synchronized (lock) {
            McqSupport.show("inside synchronized (lock)  : holdsLock = %s", Thread.holdsLock(lock));
            assertThat(Thread.holdsLock(lock)).isTrue();
        }
        assertThat(Thread.holdsLock(lock)).isFalse();
    }

    // PURPOSE: Option D is WRONG - volatile is a field modifier about memory visibility; it is not allowed on methods or classes and has nothing to do with GC.
    @Test
    void optionD_wrong_unrelatedToGarbageCollection() {
        McqSupport.show("volatile allowed on fields?  %s", (Modifier.fieldModifiers() & Modifier.VOLATILE) != 0);
        McqSupport.show("volatile allowed on methods? %s", (Modifier.methodModifiers() & Modifier.VOLATILE) != 0);
        McqSupport.show("volatile allowed on classes? %s", (Modifier.classModifiers() & Modifier.VOLATILE) != 0);
        assertThat(Modifier.fieldModifiers() & Modifier.VOLATILE).isNotZero();
        assertThat(Modifier.methodModifiers() & Modifier.VOLATILE).isZero();
        assertThat(Modifier.classModifiers() & Modifier.VOLATILE).isZero();
    }
}
