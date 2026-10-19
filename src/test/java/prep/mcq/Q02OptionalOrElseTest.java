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
 *
 * ANSWER: C
 *
 * WHY:
 *   A. Wrong - it is the other way round. orElse(x) is a normal method call, so Java evaluates x BEFORE the call (eager).
 *   B. Wrong - the number of times the fallback runs differs, which matters when it is expensive or has side effects.
 *   C. Correct - orElseGet only runs the lambda if the Optional is empty.
 *   D. Wrong - orElse supplies a default. orElseThrow() and get() are the methods that throw on an empty Optional.
 *
 * See it run:  ./run.sh mcq Q02
 */
class Q02OptionalOrElseTest {
    private final AtomicInteger calls = new AtomicInteger();

    /** Stands for an expensive computation (database call, object creation...). Counts how often it runs. */
    private String expensive() {
        calls.incrementAndGet();
        McqSupport.show("    >> expensive() was called (total calls: %d)", calls.get());
        return "fallback";
    }

    // PURPOSE: Demo - a present value: watch when the fallback computation runs for orElse and for orElseGet.
    @Test
    void demo_presentValue() {
        Optional<String> present = Optional.of("value");

        McqSupport.show("present.orElse(expensive())  ...");
        present.orElse(expensive());                 // Java evaluates expensive() BEFORE calling orElse - it is just a method argument
        assertThat(calls.get()).isEqualTo(1);

        McqSupport.show("present.orElseGet(() -> expensive())  ...");
        present.orElseGet(() -> expensive());        // the lambda is never invoked because a value is present
        McqSupport.show("  (nothing printed: the supplier was never called)");
        assertThat(calls.get()).isEqualTo(1);
    }

    // PURPOSE: Option A is WRONG - it has them reversed: orElse is the eager one.
    @Test
    void optionA_wrong_orElseIsTheEagerOne() {
        Optional<String> present = Optional.of("v");
        present.orElse(expensive());
        int afterOrElse = calls.get();
        calls.set(0);
        present.orElseGet(this::expensive);
        int afterOrElseGet = calls.get();
        McqSupport.show("fallback runs with orElse    : %d  (eager)", afterOrElse);
        McqSupport.show("fallback runs with orElseGet : %d  (lazy)", afterOrElseGet);
        assertThat(afterOrElse).isEqualTo(1);
        assertThat(afterOrElseGet).isEqualTo(0);
    }

    // PURPOSE: Option B is WRONG - not identical: for a present value the fallback runs a different number of times.
    @Test
    void optionB_wrong_notIdentical() {
        Optional<String> present = Optional.of("v");
        AtomicInteger viaOrElse = new AtomicInteger(), viaOrElseGet = new AtomicInteger();
        present.orElse(count(viaOrElse));
        present.orElseGet(() -> count(viaOrElseGet));
        McqSupport.show("orElse runs the fallback %d time(s), orElseGet %d time(s)", viaOrElse.get(), viaOrElseGet.get());
        assertThat(viaOrElse.get()).isNotEqualTo(viaOrElseGet.get());
    }

    private static String count(AtomicInteger c) {
        c.incrementAndGet();
        return "x";
    }

    // PURPOSE: Option C is CORRECT - when EMPTY both need the fallback; when PRESENT only orElse wastes the work.
    @Test
    void optionC_correct_laziness() {
        Optional<String> empty = Optional.empty();
        McqSupport.show("EMPTY optional:");
        assertThat(empty.orElse(expensive())).isEqualTo("fallback");
        assertThat(empty.orElseGet(this::expensive)).isEqualTo("fallback");
        assertThat(calls.get()).isEqualTo(2);        // both needed the fallback

        calls.set(0);
        Optional<String> present = Optional.of("v");
        McqSupport.show("PRESENT optional:");
        present.orElse(expensive());
        present.orElseGet(this::expensive);
        assertThat(calls.get()).isEqualTo(1);        // only orElse ran it
    }

    // PURPOSE: Option D is WRONG - orElse never throws for an empty Optional; orElseThrow() and get() do.
    @Test
    void optionD_wrong_orElseDoesNotThrow() {
        Optional<String> empty = Optional.empty();
        McqSupport.attempt("empty.orElse(\"x\")", () -> empty.orElse("x"));
        McqSupport.attempt("empty.orElseThrow()", empty::orElseThrow);
        McqSupport.attempt("empty.get()", empty::get);
        assertThat(empty.orElse("x")).isEqualTo("x");
        assertThatThrownBy(empty::orElseThrow).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(empty::get).isInstanceOf(NoSuchElementException.class);
    }
}
