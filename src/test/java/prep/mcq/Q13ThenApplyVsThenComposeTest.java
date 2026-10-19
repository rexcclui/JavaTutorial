package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 13 - CompletableFuture.thenApply vs thenCompose
 *
 * What is the difference between thenApply and thenCompose?
 *
 *   A. They are interchangeable.
 *   B. thenApply always runs on another thread; thenCompose runs on the calling thread.
 *   C. thenCompose is the method for handling exceptions.
 *   D. thenCompose is like flatMap: it flattens a function that itself returns a CompletionStage, so you do not end up with a nested CompletableFuture<CompletableFuture<T>>.
 *
 * ANSWER: D
 *
 * WHY:
 *   A. Wrong - the result types differ: thenApply gives a future OF a future, thenCompose gives a future of the value.
 *   B. Wrong - thenApply on an already-completed future runs right in the calling thread; only thenApplyAsync hands off to another thread.
 *   C. Wrong - errors are handled with exceptionally / handle / whenComplete.
 *   D. Correct - same idea as Stream.flatMap / Optional.flatMap.
 *
 * See it run:  ./run.sh mcq Q13
 */
class Q13ThenApplyVsThenComposeTest {

    /** An asynchronous step that returns its own future. */
    private static CompletableFuture<Integer> doubleAsync(int x) {
        return CompletableFuture.supplyAsync(() -> x * 2);
    }

    // PURPOSE: Demo - the same async step used with thenApply gives a NESTED future (two get() calls); with thenCompose it is flat.
    @Test
    void demo_nestedVersusFlat() throws Exception {
        CompletableFuture<CompletableFuture<Integer>> nested =
                CompletableFuture.completedFuture(21).thenApply(Q13ThenApplyVsThenComposeTest::doubleAsync);
        CompletableFuture<Integer> flat =
                CompletableFuture.completedFuture(21).thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync);

        McqSupport.show("thenApply   -> CompletableFuture<CompletableFuture<Integer>>: get() gives a %s", nested.get().getClass().getSimpleName());
        McqSupport.show("                                                       get().get() = %d", nested.get().get());
        McqSupport.show("thenCompose -> CompletableFuture<Integer>:                   get() = %d", flat.get());
        assertThat(nested.get().get()).isEqualTo(42);
        assertThat(flat.get()).isEqualTo(42);
    }

    // PURPOSE: Option A is WRONG - not interchangeable: the result types differ.
    @Test
    void optionA_wrong_differentResultTypes() throws Exception {
        Object viaApply = CompletableFuture.completedFuture(1).thenApply(Q13ThenApplyVsThenComposeTest::doubleAsync).get();
        Object viaCompose = CompletableFuture.completedFuture(1).thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync).get();
        McqSupport.show("thenApply   get() type: %s", viaApply.getClass().getSimpleName());
        McqSupport.show("thenCompose get() type: %s", viaCompose.getClass().getSimpleName());
        assertThat(viaApply).isInstanceOf(CompletableFuture.class);
        assertThat(viaCompose).isInstanceOf(Integer.class);
    }

    // PURPOSE: Option B is WRONG - thenApply on a completed future runs in the calling thread; only thenApplyAsync leaves it.
    @Test
    void optionB_wrong_thenApplyIsNotAlwaysAsync() throws Exception {
        String viaApply = CompletableFuture.completedFuture(1).thenApply(x -> Thread.currentThread().getName()).get();
        McqSupport.show("calling thread          : %s", Thread.currentThread().getName());
        McqSupport.show("thenApply ran on        : %s", viaApply);
        assertThat(viaApply).isEqualTo(Thread.currentThread().getName());
    }

    // PURPOSE: Option C is WRONG - thenCompose just passes a failure along; recovery is done with exceptionally/handle.
    @Test
    void optionC_wrong_composeDoesNotHandleErrors() {
        CompletableFuture<Integer> failing = CompletableFuture.failedFuture(new IllegalStateException("boom"));
        CompletableFuture<Integer> composed = failing.thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync);
        McqSupport.attempt("failing.thenCompose(...).join()", composed::join);
        McqSupport.show("failing.exceptionally(e -> -1).join() = %d   <- this is how you recover", failing.exceptionally(e -> -1).join());
        assertThatThrownBy(composed::get).isInstanceOf(ExecutionException.class).hasRootCauseInstanceOf(IllegalStateException.class);
        assertThat(failing.exceptionally(e -> -1).join()).isEqualTo(-1);
    }

    // PURPOSE: Option D is CORRECT - chaining two async steps with thenCompose reads like a straight line.
    @Test
    void optionD_correct_composeChainsAsyncSteps() {
        int result = CompletableFuture.completedFuture(3)
                .thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync)
                .thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync)
                .join();
        McqSupport.show("3 -> doubleAsync -> doubleAsync = %d", result);
        assertThat(result).isEqualTo(12);
    }
}
