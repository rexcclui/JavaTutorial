package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 8 - Thread.run() vs Thread.start()
 *
 * What happens when you call run() directly on a Thread object instead of start()?
 *
 *   A. A new thread is created and the code runs in it.
 *   B. An IllegalThreadStateException is thrown.
 *   C. The code never runs.
 *   D. The code runs in the calling thread; no new thread is created.
 *
 * ANSWER: D
 *
 * WHY:
 *   A. Wrong - only start() asks the JVM to create a new thread.
 *   B. Wrong - IllegalThreadStateException is thrown when you call start() a SECOND time, not for run().
 *   C. Wrong - the body does run, just in the wrong thread.
 *   D. Correct - run() is an ordinary method call.
 *
 * See it run:  ./run.sh mcq Q08
 */
class Q08ThreadRunVsStartTest {

    // PURPOSE: Demo - print the name of the thread that executes the body, once via run() and once via start().
    @Test
    void demo_whichThreadRunsTheBody() throws Exception {
        AtomicReference<String> ranOn = new AtomicReference<>();
        Thread t = new Thread(() -> ranOn.set(Thread.currentThread().getName()), "worker-1");

        McqSupport.show("calling thread          : %s", Thread.currentThread().getName());
        t.run();
        McqSupport.show("t.run()   body ran on   : %s   <- the calling thread, no new thread", ranOn.get());
        assertThat(ranOn.get()).isEqualTo(Thread.currentThread().getName());

        Thread t2 = new Thread(() -> ranOn.set(Thread.currentThread().getName()), "worker-2");
        t2.start();
        t2.join();
        McqSupport.show("t2.start() body ran on  : %s   <- a real new thread", ranOn.get());
        assertThat(ranOn.get()).isEqualTo("worker-2");
    }

    // PURPOSE: Option A is WRONG - after run() the thread was never started (state NEW).
    @Test
    void optionA_wrong_runCreatesNoThread() {
        Thread t = new Thread(() -> { });
        t.run();
        McqSupport.show("state after run() = %s, isAlive = %s", t.getState(), t.isAlive());
        assertThat(t.getState()).isEqualTo(Thread.State.NEW);
        assertThat(t.isAlive()).isFalse();
    }

    // PURPOSE: Option B is WRONG - run() can be called any number of times; the exception belongs to calling start() twice.
    @Test
    void optionB_wrong_exceptionIsForStartingTwice() throws Exception {
        Thread t = new Thread(() -> { });
        McqSupport.attempt("t.run()", t::run);
        McqSupport.attempt("t.run() again", t::run);
        t.start();
        t.join();
        McqSupport.attempt("t.start() a second time", t::start);
        assertThatThrownBy(t::start).isInstanceOf(IllegalThreadStateException.class);
    }

    // PURPOSE: Option C is WRONG - the body does run.
    @Test
    void optionC_wrong_bodyDoesRun() {
        AtomicReference<Boolean> ran = new AtomicReference<>(false);
        new Thread(() -> ran.set(true)).run();
        McqSupport.show("body executed: %s", ran.get());
        assertThat(ran.get()).isTrue();
    }
}
