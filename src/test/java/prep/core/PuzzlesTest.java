package prep.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * PREDICT THE OUTPUT. For each puzzle, read the code, write down your answer,
 * THEN look at the assertion. To test yourself, change an expected value to your
 * (wrong) guess and see the failure message, or add your own puzzles.
 */
class PuzzlesTest {
    // PURPOSE: Boxed Integers: == works for -128..127 (cached) but not outside it. Always use equals() for wrapper types.
    @Test
    void integerCache() {
        Integer a = 127, b = 127, c = 128, d = 128;
        assertThat(a == b).isTrue();   // cached range -128..127
        assertThat(c == d).isFalse();  // new objects -> reference compare
        assertThat(c.equals(d)).isTrue();
    }

    // PURPOSE: String literals share one pooled instance; new String(...) does not. intern() returns the pooled copy.
    @Test
    void stringPool() {
        String a = "hi";
        String b = "hi";
        String c = new String("hi");
        assertThat(a == b).isTrue();
        assertThat(a == c).isFalse();
        assertThat(a == c.intern()).isTrue();
    }

    // PURPOSE: Java is pass-by-value: a method can mutate the object a reference points to, but cannot re-point the caller's variable.
    @Test
    void passByValue() {
        StringBuilder sb = new StringBuilder("a");
        mutate(sb);
        assertThat(sb.toString()).isEqualTo("ab");   // object mutated through the copy of the reference
        reassign(sb);
        assertThat(sb.toString()).isEqualTo("ab");   // reassigning the local reference changes nothing
    }

    private static void mutate(StringBuilder sb) { sb.append("b"); }
    private static void reassign(StringBuilder sb) { sb = new StringBuilder("zzz"); }

    // PURPOSE: A return inside finally replaces the return in try. Never do this in real code, but interviewers ask.
    @Test
    void finallyOverridesReturn() {
        assertThat(tricky()).isEqualTo(2);
    }

    @SuppressWarnings("finally")
    private static int tricky() {
        try { return 1; } finally { return 2; }
    }

    // PURPOSE: char + int is an int; compound assignment (c += 1) adds an implicit cast so it stays a char.
    @Test
    void charArithmetic() {
        char c = 'a';
        c += 1;
        assertThat(c).isEqualTo('b');
        assertThat('a' + 1).isEqualTo(98);  // int, not char
    }

    // PURPOSE: List<Integer>.remove(int index) vs remove(Object): overload resolution picks different methods.
    @Test
    void removeWhileIterating() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        list.remove(Integer.valueOf(1));  // remove(Object)
        list.remove(0);                   // remove(int index)
        assertThat(list).containsExactly(3);
    }

    // PURPOSE: int arithmetic wraps around silently; Math.abs(Integer.MIN_VALUE) is still negative.
    @Test
    void integerOverflow() {
        assertThat(Integer.MAX_VALUE + 1).isEqualTo(Integer.MIN_VALUE);
        assertThat(Math.abs(Integer.MIN_VALUE)).isEqualTo(Integer.MIN_VALUE);
    }

    // PURPOSE: Binary floating point cannot represent 0.1 exactly, so compare with a tolerance (or use BigDecimal for money).
    @Test
    void floatingPoint() {
        assertThat(0.1 + 0.2 == 0.3).isFalse();
        assertThat(Math.abs(0.1 + 0.2 - 0.3) < 1e-9).isTrue();
    }
}
