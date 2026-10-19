package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 30 - exceptions from tasks
 *
 *     Future<Integer> f = executor.submit(() -> { throw new IllegalStateException("boom"); });
 *     f.get();
 *
 * What does f.get() throw?
 *
 *   A. IllegalStateException directly
 *   B. Nothing; it returns null
 *   C. InterruptedException
 *   D. ExecutionException, whose cause is the IllegalStateException
 *
 * ANSWER: D
 *
 * WHY:
 *   submit() captures any exception thrown by the task inside the Future. get() re-throws it wrapped in ExecutionException; use getCause() to reach the original.
 *   (CompletableFuture.join() wraps it in CompletionException instead.)
 *   C. InterruptedException is thrown only if the CALLING thread is interrupted while waiting.
 *   B. A task that failed has no result to return.
 *
 * See it run:  ./run.sh mcq Q30
 */
class Q30FutureGetExceptionTest {

    // PURPOSE: Demo - submit a failing task and print what get() throws and what its cause is.
    @Test
    void demo_getWrapsTheException() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<Integer> f = executor.submit(() -> { throw new IllegalStateException("boom"); });
            try {
                f.get();
            } catch (ExecutionException e) {
                McqSupport.show("f.get() threw   : %s", e.getClass().getSimpleName());
                McqSupport.show("e.getCause()    : %s: %s", e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
                assertThat(e.getCause()).isInstanceOf(IllegalStateException.class).hasMessage("boom");
            }
        } finally {
            executor.shutdown();
        }
    }

    // PURPOSE: Options A and B are WRONG - get() throws, and what it throws is the wrapper, not the original exception.
    @Test
    void optionsAB_wrong_getThrowsAWrapper() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<Integer> f = executor.submit(() -> { throw new IllegalStateException("boom"); });
            assertThatThrownBy(f::get).isInstanceOf(ExecutionException.class).isNotInstanceOf(IllegalStateException.class);
            McqSupport.show("f.isDone() = %s   (a failed task is 'done')", f.isDone());
        } finally {
            executor.shutdown();
        }
    }

    // PURPOSE: Option C is WRONG - InterruptedException is about the WAITING thread being interrupted, not the task failing.
    @Test
    void optionC_wrong_interruptedExceptionIsForTheWaiter() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<Integer> slow = executor.submit(() -> { Thread.sleep(5000); return 1; });
            Thread.currentThread().interrupt();                       // interrupt ourselves, then wait
            McqSupport.attempt("slow.get() while interrupted", () -> {
                try { slow.get(); } catch (InterruptedException e) { throw new IllegalStateException("InterruptedException"); }
                catch (ExecutionException e) { throw new IllegalStateException(e); }
            });
            Thread.interrupted();                                     // clear the flag so other tests are unaffected
            slow.cancel(true);
        } finally {
            executor.shutdownNow();
        }
    }

    // PURPOSE: Option D is CORRECT - and note the sibling API: CompletableFuture.join() wraps in CompletionException.
    @Test
    void optionD_correct_andCompletableFutureIsDifferent() {
        CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> { throw new IllegalStateException("boom"); });
        assertThatThrownBy(cf::get).isInstanceOf(ExecutionException.class).hasCauseInstanceOf(IllegalStateException.class);
        assertThatThrownBy(cf::join).isInstanceOf(CompletionException.class).hasCauseInstanceOf(IllegalStateException.class);
        McqSupport.show("CompletableFuture: get() -> ExecutionException (checked), join() -> CompletionException (unchecked)");
    }
}
