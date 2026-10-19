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
 */
class Q08ThreadRunVsStartTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'D';

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - record the name of the thread that executes the body, once via run() and once via start().
    @Test
    void concept_whichThreadRunsTheBody() throws Exception {
        AtomicReference<String> ranOn = new AtomicReference<>();
        Thread t = new Thread(() -> ranOn.set(Thread.currentThread().getName()), "worker-1");

        t.run();                                                         // plain method call
        assertThat(ranOn.get()).isEqualTo(Thread.currentThread().getName());

        Thread t2 = new Thread(() -> ranOn.set(Thread.currentThread().getName()), "worker-2");
        t2.start();                                                      // really starts a new thread
        t2.join();
        assertThat(ranOn.get()).isEqualTo("worker-2");
    }

    // PURPOSE: Experiment for option A - after run(), the thread object was never started (state NEW), so no new thread was created.
    @Test
    void experimentA_runCreatesNoThread() {
        Thread t = new Thread(() -> { });
        t.run();
        assertThat(t.getState()).isEqualTo(Thread.State.NEW);
        assertThat(t.isAlive()).isFalse();
        // Verdict: A is false.
    }

    // PURPOSE: Experiment for option B - IllegalThreadStateException is real, but it comes from calling start() TWICE, not from run().
    @Test
    void experimentB_exceptionIsForStartingTwice() throws Exception {
        Thread t = new Thread(() -> { });
        t.run();
        t.run();                                                         // run() any number of times: fine
        t.start();
        t.join();
        assertThatThrownBy(t::start).isInstanceOf(IllegalThreadStateException.class);
        // Verdict: B is false for run(); a thread can be started only once.
    }

    // PURPOSE: Experiment for option C - the body does run (just in the wrong thread).
    @Test
    void experimentC_bodyDoesRun() {
        AtomicReference<Boolean> ran = new AtomicReference<>(false);
        new Thread(() -> ran.set(true)).run();
        assertThat(ran.get()).isTrue();
        // Verdict: C is false.
    }
}
