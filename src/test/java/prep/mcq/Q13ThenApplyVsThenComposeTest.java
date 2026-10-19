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
 */
class Q13ThenApplyVsThenComposeTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'D';

    /** An asynchronous step that returns its own future. */
    private static CompletableFuture<Integer> doubleAsync(int x) {
        return CompletableFuture.supplyAsync(() -> x * 2);
    }

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - the same async step used with thenApply gives a NESTED future; with thenCompose it is flattened.
    @Test
    void concept_nestedVersusFlat() throws Exception {
        CompletableFuture<CompletableFuture<Integer>> nested =
                CompletableFuture.completedFuture(21).thenApply(Q13ThenApplyVsThenComposeTest::doubleAsync);
        CompletableFuture<Integer> flat =
                CompletableFuture.completedFuture(21).thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync);

        assertThat(nested.get().get()).isEqualTo(42);    // two get() calls needed
        assertThat(flat.get()).isEqualTo(42);            // one
    }

    // PURPOSE: Experiment for option A - not interchangeable: the result TYPES differ (a future of a future vs a future of the value).
    @Test
    void experimentA_differentResultTypes() throws Exception {
        Object viaApply = CompletableFuture.completedFuture(1).thenApply(Q13ThenApplyVsThenComposeTest::doubleAsync).get();
        Object viaCompose = CompletableFuture.completedFuture(1).thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync).get();
        assertThat(viaApply).isInstanceOf(CompletableFuture.class);
        assertThat(viaCompose).isInstanceOf(Integer.class);
        // Verdict: A is false.
    }

    // PURPOSE: Experiment for option B - thenApply on an already-completed future runs the function right here in the calling thread.
    @Test
    void experimentB_thenApplyIsNotAlwaysAsync() throws Exception {
        String ranOn = CompletableFuture.completedFuture(1).thenApply(x -> Thread.currentThread().getName()).get();
        assertThat(ranOn).isEqualTo(Thread.currentThread().getName());
        // Only thenApplyAsync(...) hands the work to another thread (the common pool by default).
        // Verdict: B is false.
    }

    // PURPOSE: Experiment for option C - error handling is done with exceptionally/handle/whenComplete; thenCompose just passes a failure along.
    @Test
    void experimentC_composeDoesNotHandleErrors() {
        CompletableFuture<Integer> failing = CompletableFuture.failedFuture(new IllegalStateException("boom"));
        CompletableFuture<Integer> composed = failing.thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync);
        assertThatThrownBy(composed::get).isInstanceOf(ExecutionException.class).hasRootCauseInstanceOf(IllegalStateException.class);

        assertThat(failing.exceptionally(e -> -1).join()).isEqualTo(-1);   // this is how you recover
        // Verdict: C is false.
    }

    // PURPOSE: Experiment for option D - chaining two async steps with thenCompose reads like a straight line; same idea as Optional.flatMap / Stream.flatMap.
    @Test
    void experimentD_composeChainsAsyncSteps() {
        int result = CompletableFuture.completedFuture(3)
                .thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync)     // 6
                .thenCompose(Q13ThenApplyVsThenComposeTest::doubleAsync)     // 12
                .join();
        assertThat(result).isEqualTo(12);
        // Verdict: D is true.
    }
}
