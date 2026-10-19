package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * QUESTION 21 - Java is pass-by-value
 *
 *     static void change(StringBuilder sb, String s, int n) {
 *         sb.append("!");
 *         s = s + "!";
 *         n++;
 *     }
 *     ...
 *     StringBuilder sb = new StringBuilder("a"); String s = "a"; int n = 1;
 *     change(sb, s, n);
 *     System.out.println(sb + " " + s + " " + n);
 *
 * What does it print?
 *
 *   A. a! a 1
 *   B. a a 1
 *   C. a! a! 2
 *   D. a! a! 1
 *
 * ANSWER: A
 *
 * WHY:
 *   Java copies the argument VALUE into the parameter.
 *   - n: the int itself is copied; n++ changes only the copy.
 *   - s: the reference is copied; s = s + "!" makes the COPY point to a new String; the caller's s still points to "a".
 *   - sb: the reference is copied, but both references point to the SAME object, so append("!") is visible to the caller.
 *
 * See it run:  ./run.sh mcq Q21
 */
class Q21PassByValueTest {

    static void change(StringBuilder sb, String s, int n) {
        sb.append("!");
        s = s + "!";
        n++;
        McqSupport.show("  inside change(): sb = %s, s = %s, n = %d", sb, s, n);
    }

    // PURPOSE: Demo - the exact snippet, with before / inside / after printed so you can see which variable the caller still sees changed.
    @Test
    void demo_beforeInsideAfter() {
        StringBuilder sb = new StringBuilder("a");
        String s = "a";
        int n = 1;
        McqSupport.show("before : sb = %s, s = %s, n = %d", sb, s, n);
        change(sb, s, n);
        McqSupport.show("after  : sb = %s, s = %s, n = %d", sb, s, n);
        assertThat(sb + " " + s + " " + n).isEqualTo("a! a 1");
    }

    // PURPOSE: The classic proof that Java is NOT pass-by-reference: a swap method cannot swap the caller's variables.
    @Test
    void proof_swapDoesNotWork() {
        StringBuilder x = new StringBuilder("X"), y = new StringBuilder("Y");
        swap(x, y);
        McqSupport.show("after swap(x, y): x = %s, y = %s   (unchanged: only the local copies of the references were swapped)", x, y);
        assertThat(x.toString()).isEqualTo("X");
        assertThat(y.toString()).isEqualTo("Y");
    }

    private static void swap(StringBuilder a, StringBuilder b) {
        StringBuilder tmp = a;
        a = b;
        b = tmp;
    }

    // PURPOSE: What you CAN do with a copied reference: change the object it points to.
    @Test
    void whatYouCanDo_mutateTheSharedObject() {
        StringBuilder x = new StringBuilder("X");
        mutate(x);
        McqSupport.show("after mutate(x): x = %s", x);
        assertThat(x.toString()).isEqualTo("X!");
    }

    private static void mutate(StringBuilder a) {
        a.append("!");
    }
}
