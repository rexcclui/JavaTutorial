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
    @Test
    void integerCache() {
        Integer a = 127, b = 127, c = 128, d = 128;
        assertThat(a == b).isTrue();   // cached range -128..127
        assertThat(c == d).isFalse();  // new objects -> reference compare
        assertThat(c.equals(d)).isTrue();
    }

    @Test
    void stringPool() {
        String a = "hi";
        String b = "hi";
        String c = new String("hi");
        assertThat(a == b).isTrue();
        assertThat(a == c).isFalse();
        assertThat(a == c.intern()).isTrue();
    }

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

    @Test
    void finallyOverridesReturn() {
        assertThat(tricky()).isEqualTo(2);
    }

    @SuppressWarnings("finally")
    private static int tricky() {
        try { return 1; } finally { return 2; }
    }

    @Test
    void charArithmetic() {
        char c = 'a';
        c += 1;
        assertThat(c).isEqualTo('b');
        assertThat('a' + 1).isEqualTo(98);  // int, not char
    }

    @Test
    void removeWhileIterating() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        list.remove(Integer.valueOf(1));  // remove(Object)
        list.remove(0);                   // remove(int index)
        assertThat(list).containsExactly(3);
    }

    @Test
    void integerOverflow() {
        assertThat(Integer.MAX_VALUE + 1).isEqualTo(Integer.MIN_VALUE);
        assertThat(Math.abs(Integer.MIN_VALUE)).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void floatingPoint() {
        assertThat(0.1 + 0.2 == 0.3).isFalse();
        assertThat(Math.abs(0.1 + 0.2 - 0.3) < 1e-9).isTrue();
    }
}
