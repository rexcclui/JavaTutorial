package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 34 - arithmetic on small types
 *
 *     byte b = 10;
 *
 * Which of these lines does NOT compile?
 *
 *   A. byte c = 10;
 *   B. b += 1;
 *   C. b++;
 *   D. b = b + 1;
 *
 * ANSWER: D
 *
 * WHY:
 *   In an arithmetic expression byte, short and char are first PROMOTED to int, so b + 1 is an int. Assigning an int to a byte needs an explicit cast
 *   (error: "incompatible types: possible lossy conversion from int to byte").
 *   Compound assignment (+=) and ++ include an implicit cast back to the variable's type, so B and C compile. A is a constant that fits in a byte.
 *
 * See it run:  ./run.sh mcq Q34
 */
class Q34ByteArithmeticTest {

    private static List<String> compile(String statement) {
        return McqSupport.compileErrors("Snippet", "public class Snippet { void m() { byte b = 10; " + statement + " } }");
    }

    // PURPOSE: Demo - the real compiler's verdict on each candidate line.
    @Test
    void demo_compilerVerdicts() {
        String[] lines = {"byte c = 10;", "b += 1;", "b++;", "b = b + 1;"};
        for (int i = 0; i < lines.length; i++) {
            var errors = compile(lines[i]);
            McqSupport.show("%c. %-14s -> %s", 'A' + i, lines[i], errors.isEmpty() ? "compiles" : "ERROR: " + errors.get(0));
        }
        assertThat(compile(lines[0])).isEmpty();
        assertThat(compile(lines[1])).isEmpty();
        assertThat(compile(lines[2])).isEmpty();
        assertThat(compile(lines[3])).hasSize(1);
        assertThat(compile(lines[3]).get(0)).contains("possible lossy conversion from int to byte");
    }

    // PURPOSE: Why: b + 1 has type int. Proven by assigning it to an int (fine) and by overflow behaviour of the compound forms.
    @Test
    void why_bPlusOneIsAnInt() {
        byte b = 10;
        int sum = b + 1;
        McqSupport.show("byte b = 10;  int sum = b + 1;  -> %d (the expression is an int)", sum);
        assertThat(sum).isEqualTo(11);
    }

    // PURPOSE: Fix - an explicit cast makes b = b + 1 legal, and compound assignment silently wraps around on overflow.
    @Test
    void fix_castAndOverflowWrapAround() {
        byte b = 127;
        b += 1;                                                   // implicit cast back to byte: wraps around
        McqSupport.show("byte b = 127; b += 1;  -> b = %d   (overflow wraps to the minimum)", b);
        byte c = 10;
        c = (byte) (c + 1);
        McqSupport.show("c = (byte) (c + 1)     -> c = %d", c);
        assertThat(b).isEqualTo((byte) -128);
        assertThat(c).isEqualTo((byte) 11);
        assertThat(compile("b = (byte) (b + 1);")).isEmpty();
    }

    // PURPOSE: Same rule for char: 'a' + 1 is an int, but c++ and c += 1 keep the char type.
    @Test
    void sameRuleForChar() {
        char c = 'a';
        c += 1;
        c++;
        McqSupport.show("char c = 'a'; c += 1; c++;  -> c = '%c'", c);
        McqSupport.show("'a' + 1 = %d   (an int, not the character 'b')", 'a' + 1);
        assertThat(c).isEqualTo('c');
        assertThat('a' + 1).isEqualTo(98);
    }
}
