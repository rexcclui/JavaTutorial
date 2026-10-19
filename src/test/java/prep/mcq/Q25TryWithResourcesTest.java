package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 25 - try-with-resources order
 *
 *     try (Res a = new Res("A"); Res b = new Res("B")) {
 *         System.out.println("body");
 *     }
 *
 * Each Res prints "open X" in its constructor and "close X" in close(). What is the order of the output?
 *
 *   A. open A, open B, body, close B, close A
 *   B. open A, open B, body, close A, close B
 *   C. open B, open A, body, close B, close A
 *   D. body, open A, open B, close B, close A
 *
 * ANSWER: A
 *
 * WHY:
 *   Resources are opened in the order declared (A then B) and closed in REVERSE order (B then A), like nested try/finally blocks.
 *   Close happens even if the body throws. If body AND close both throw, the body's exception wins and the close exception is attached as "suppressed".
 *
 * See it run:  ./run.sh mcq Q25
 */
class Q25TryWithResourcesTest {

    static class Res implements AutoCloseable {
        private final String name;
        private final List<String> log;
        private final boolean failOnClose;

        Res(String name, List<String> log) { this(name, log, false); }

        Res(String name, List<String> log, boolean failOnClose) {
            this.name = name;
            this.log = log;
            this.failOnClose = failOnClose;
            log.add("open " + name);
        }

        @Override
        public void close() {
            log.add("close " + name);
            if (failOnClose) throw new IllegalStateException("close failed: " + name);
        }
    }

    // PURPOSE: Demo - the exact snippet from the question; print the order of events.
    @Test
    void demo_theExactSnippet() {
        List<String> log = new ArrayList<>();
        try (Res a = new Res("A", log); Res b = new Res("B", log)) {
            log.add("body");
        }
        McqSupport.show("order of events: %s", log);
        assertThat(log).containsExactly("open A", "open B", "body", "close B", "close A");
    }

    // PURPOSE: Options B, C, D are WRONG - each differs from the real order; the real order is printed above.
    @Test
    void optionsBCD_wrong_differFromTheRealOrder() {
        List<String> log = new ArrayList<>();
        try (Res a = new Res("A", log); Res b = new Res("B", log)) {
            log.add("body");
        }
        assertThat(log).isNotEqualTo(List.of("open A", "open B", "body", "close A", "close B"));
        assertThat(log).isNotEqualTo(List.of("open B", "open A", "body", "close B", "close A"));
        assertThat(log).isNotEqualTo(List.of("body", "open A", "open B", "close B", "close A"));
        McqSupport.show("last opened is first closed (like a stack): %s", log);
    }

    // PURPOSE: Resources are closed even when the body throws.
    @Test
    void extra_closedEvenWhenBodyThrows() {
        List<String> log = new ArrayList<>();
        try (Res a = new Res("A", log)) {
            throw new IllegalArgumentException("body failed");
        } catch (IllegalArgumentException e) {
            log.add("caught: " + e.getMessage());
        }
        McqSupport.show("order of events: %s", log);
        assertThat(log).containsExactly("open A", "close A", "caught: body failed");   // close happens BEFORE the catch block
    }

    // PURPOSE: Extra - if both the body and close() throw, the body exception is thrown and the close exception is SUPPRESSED (attached), not lost.
    @Test
    void extra_suppressedException() {
        List<String> log = new ArrayList<>();
        try (Res a = new Res("A", log, true)) {
            throw new IllegalArgumentException("body failed");
        } catch (IllegalArgumentException e) {
            McqSupport.show("thrown      : %s", e.getMessage());
            McqSupport.show("suppressed  : %s", (Object) e.getSuppressed()[0].getMessage());
            assertThat(e.getSuppressed()).hasSize(1);
            assertThat(e.getSuppressed()[0]).hasMessage("close failed: A");
        }
    }
}
