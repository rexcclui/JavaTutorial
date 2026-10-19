package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 31 - synchronized static vs instance methods
 *
 * A class has a synchronized INSTANCE method and a synchronized STATIC method. Do they block each other?
 *
 *   A. Yes, they share one lock.
 *   B. No: the instance method locks `this`, the static method locks the Class object, so they are different monitors.
 *   C. Yes, but only when called from the same thread.
 *   D. Static methods cannot be synchronized.
 *
 * ANSWER: B
 *
 * WHY:
 *   synchronized instance method  == synchronized (this) { ... }
 *   synchronized static method    == synchronized (Account.class) { ... }
 *   Two different locks, so a thread inside one does not stop another thread entering the other.
 *   (Two threads calling the SAME instance method on DIFFERENT objects do not block each other either - different `this`.)
 *
 * See it run:  ./run.sh mcq Q31
 */
class Q31SynchronizedStaticVsInstanceTest {
    static class Account {
        final CountDownLatch inside = new CountDownLatch(1);
        final CountDownLatch release = new CountDownLatch(1);

        synchronized boolean[] instanceMethod() {
            return new boolean[] {Thread.holdsLock(this), Thread.holdsLock(Account.class)};
        }

        static synchronized boolean[] staticMethod(Account a) {
            return new boolean[] {Thread.holdsLock(a), Thread.holdsLock(Account.class)};
        }

        /** Holds the instance lock until release is counted down. */
        synchronized void holdInstanceLock() throws InterruptedException {
            inside.countDown();
            release.await(5, TimeUnit.SECONDS);
        }
    }

    // PURPOSE: Demo - which monitors does each kind of synchronized method hold? (Thread.holdsLock)
    @Test
    void demo_whichLockIsHeld() {
        Account a = new Account();
        boolean[] inInstance = a.instanceMethod();
        boolean[] inStatic = Account.staticMethod(a);
        McqSupport.show("inside synchronized INSTANCE method: holds this = %s, holds Account.class = %s", inInstance[0], inInstance[1]);
        McqSupport.show("inside synchronized STATIC method  : holds this = %s, holds Account.class = %s", inStatic[0], inStatic[1]);
        assertThat(inInstance).containsExactly(true, false);
        assertThat(inStatic).containsExactly(false, true);
    }

    // PURPOSE: Option B is CORRECT - thread 1 sits inside the instance method holding `this`; main still enters the static method immediately.
    @Test
    void optionB_correct_theyDoNotBlockEachOther() throws Exception {
        Account a = new Account();
        Thread holder = new Thread(() -> {
            try { a.holdInstanceLock(); } catch (InterruptedException ignored) { }
        });
        holder.start();
        assertThat(a.inside.await(5, TimeUnit.SECONDS)).isTrue();         // holder is now inside the instance-synchronized method
        McqSupport.show("holder thread is inside the synchronized instance method (holding this)...");

        long start = System.nanoTime();
        boolean[] held = Account.staticMethod(a);                         // would wait here if they shared a lock
        long millis = (System.nanoTime() - start) / 1_000_000;
        McqSupport.show("...and main entered the synchronized static method straight away (%d ms, holds Account.class = %s)", millis, held[1]);
        assertThat(held[1]).isTrue();
        assertThat(millis).isLessThan(1000);
        a.release.countDown();
        holder.join(2000);
    }

    // PURPOSE: Contrast - two threads on the SAME lock DO block: the second waits until the first leaves (shown by the blocked thread's state).
    @Test
    void contrast_sameLockDoesBlock() throws Exception {
        Account a = new Account();
        Thread holder = new Thread(() -> {
            try { a.holdInstanceLock(); } catch (InterruptedException ignored) { }
        });
        holder.start();
        assertThat(a.inside.await(5, TimeUnit.SECONDS)).isTrue();

        Thread second = new Thread(a::instanceMethod);                    // needs the same `this` lock
        second.start();
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (second.getState() != Thread.State.BLOCKED && System.nanoTime() < deadline) Thread.onSpinWait();
        McqSupport.show("second thread calling the SAME instance method: state = %s", second.getState());
        assertThat(second.getState()).isEqualTo(Thread.State.BLOCKED);
        a.release.countDown();
        holder.join(2000);
        second.join(2000);
    }

    // PURPOSE: Option D is WRONG - static methods can be synchronized (they lock the Class object).
    @Test
    void optionD_wrong_staticCanBeSynchronized() throws Exception {
        var m = Account.class.getDeclaredMethod("staticMethod", Account.class);
        McqSupport.show("staticMethod modifiers: %s", java.lang.reflect.Modifier.toString(m.getModifiers()));
        assertThat(java.lang.reflect.Modifier.isSynchronized(m.getModifiers())).isTrue();
    }
}
