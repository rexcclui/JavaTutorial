package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 2 - Optional.orElse vs orElseGet
 *
 * What is the difference between Optional.orElse(T other) and Optional.orElseGet(Supplier<T> supplier)?
 *
 *   A. orElse() evaluates its argument lazily; orElseGet() evaluates it eagerly.
 *   B. They are identical; orElseGet() only exists so you can pass a lambda.
 *   C. orElseGet() calls its Supplier only when the Optional is empty, while the argument of orElse()
 *      is always evaluated, even when a value is present.
 *   D. orElse() throws NoSuchElementException when the Optional is empty.
 */
class Q02OptionalOrElseTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'C';

    private final AtomicInteger calls = new AtomicInteger();

    /** Stands for an expensive computation (database call, object creation...). Counts how often it runs. */
    private String expensive() {
        calls.incrementAndGet();
        return "fallback";
    }

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - count how many times the 'expensive' fallback runs for a PRESENT value with each method.
    @Test
    void concept_countFallbackCalls() {
        Optional<String> present = Optional.of("value");

        present.orElse(expensive());                 // Java evaluates expensive() BEFORE calling orElse - it is just a method argument
        assertThat(calls.get()).isEqualTo(1);

        present.orElseGet(() -> expensive());        // the lambda is never invoked because a value is present
        assertThat(calls.get()).isEqualTo(1);        // still 1
    }

    // PURPOSE: Experiment for option A - it claims orElse is lazy. Check which one actually ran the fallback for a present value.
    @Test
    void experimentA_orElseIsTheEagerOne() {
        Optional<String> present = Optional.of("v");
        present.orElse(expensive());
        int afterOrElse = calls.get();
        calls.set(0);
        present.orElseGet(this::expensive);
        int afterOrElseGet = calls.get();

        assertThat(afterOrElse).isEqualTo(1);        // orElse: ran although not needed (eager)
        assertThat(afterOrElseGet).isEqualTo(0);     // orElseGet: did not run (lazy)
        // Verdict: A has it backwards.
    }

    // PURPOSE: Experiment for option B - if they were identical, the fallback call counts would be equal. They are not.
    @Test
    void experimentB_notIdentical() {
        Optional<String> present = Optional.of("v");
        AtomicInteger viaOrElse = new AtomicInteger(), viaOrElseGet = new AtomicInteger();
        present.orElse(count(viaOrElse));
        present.orElseGet(() -> count(viaOrElseGet));
        assertThat(viaOrElse.get()).isNotEqualTo(viaOrElseGet.get());
        // Verdict: B is false. The difference matters when the fallback is expensive or has side effects.
    }

    private static String count(AtomicInteger c) {
        c.incrementAndGet();
        return "x";
    }

    // PURPOSE: Experiment for option C - the full truth: when EMPTY both give the fallback; when PRESENT only orElse wastes work.
    @Test
    void experimentC_laziness() {
        Optional<String> empty = Optional.empty();
        assertThat(empty.orElse(expensive())).isEqualTo("fallback");
        assertThat(empty.orElseGet(this::expensive)).isEqualTo("fallback");
        assertThat(calls.get()).isEqualTo(2);        // empty: both needed the fallback

        calls.set(0);
        Optional<String> present = Optional.of("v");
        present.orElse(expensive());
        present.orElseGet(this::expensive);
        assertThat(calls.get()).isEqualTo(1);        // present: only orElse ran it
        // Verdict: C is true.
    }

    // PURPOSE: Experiment for option D - which method throws on an empty Optional? orElse does not; orElseThrow() and get() do.
    @Test
    void experimentD_orElseDoesNotThrow() {
        Optional<String> empty = Optional.empty();
        assertThat(empty.orElse("x")).isEqualTo("x");
        assertThatThrownBy(empty::orElseThrow).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(empty::get).isInstanceOf(NoSuchElementException.class);
        // Verdict: D is false. orElse supplies a default; it never throws for an empty Optional.
    }
}
