package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 20 - List<Integer>.remove(int) vs remove(Object)
 *
 *     List<Integer> list = new ArrayList<>(List.of(10, 20, 30));
 *     list.remove(1);
 *     System.out.println(list);
 *
 * What does it print?
 *
 *   A. [20, 30]
 *   B. [10, 20]
 *   C. [10, 30]
 *   D. It throws an exception.
 *
 * ANSWER: C
 *
 * WHY:
 *   The literal 1 is an int, so Java picks remove(int index): it removes the element AT INDEX 1 (the value 20) -> [10, 30].
 *   To remove the VALUE 1 you must call remove(Integer.valueOf(1)) or remove((Object) 1), which selects remove(Object).
 *   A is what you get with remove(Integer.valueOf(10)). B would remove index 2. D would happen with remove(5): IndexOutOfBounds.
 *
 * See it run:  ./run.sh mcq Q20
 */
class Q20ListRemoveOverloadTest {

    private static List<Integer> list() {
        return new ArrayList<>(List.of(10, 20, 30));
    }

    // PURPOSE: Demo - the same list, three different remove calls and what each one removed.
    @Test
    void demo_whichOverloadIsChosen() {
        List<Integer> a = list();
        Integer removedByIndex = a.remove(1);
        McqSupport.show("remove(1)                      -> removed the ELEMENT AT INDEX 1 (value %d)  -> %s", removedByIndex, a);

        List<Integer> b = list();
        boolean removedValue = b.remove(Integer.valueOf(10));
        McqSupport.show("remove(Integer.valueOf(10))    -> removed the VALUE 10 (%s)                -> %s", removedValue, b);

        List<Integer> c = list();
        boolean removedValue2 = c.remove((Object) 20);
        McqSupport.show("remove((Object) 20)            -> removed the VALUE 20 (%s)                -> %s", removedValue2, c);
        assertThat(a).containsExactly(10, 30);
        assertThat(b).containsExactly(20, 30);
        assertThat(c).containsExactly(10, 30);
    }

    // PURPOSE: Option C is CORRECT - remove(1) with an int literal removes index 1.
    @Test
    void optionC_correct_removesIndexOne() {
        List<Integer> l = list();
        l.remove(1);
        McqSupport.show("after list.remove(1) -> %s", l);
        assertThat(l).containsExactly(10, 30);
    }

    // PURPOSE: Option A is WRONG - [20, 30] needs the VALUE 10 removed, which takes remove(Integer.valueOf(10)).
    @Test
    void optionA_wrong_thatIsRemovingTheValue10() {
        List<Integer> l = list();
        l.remove(Integer.valueOf(10));
        assertThat(l).containsExactly(20, 30);
    }

    // PURPOSE: Option B is WRONG - [10, 20] would need index 2 removed.
    @Test
    void optionB_wrong_thatIsRemovingIndexTwo() {
        List<Integer> l = list();
        l.remove(2);
        assertThat(l).containsExactly(10, 20);
    }

    // PURPOSE: Option D is WRONG for index 1 - but an index that does not exist (remove(5)) really throws IndexOutOfBoundsException.
    @Test
    void optionD_wrong_butBadIndexThrows() {
        McqSupport.attempt("list.remove(1)", () -> list().remove(1));
        McqSupport.attempt("list.remove(5)", () -> list().remove(5));
        McqSupport.attempt("list.remove(Integer.valueOf(5))", () -> list().remove(Integer.valueOf(5)));
        assertThat(list().remove(Integer.valueOf(5))).isFalse();   // value not present: returns false, no exception
    }
}
