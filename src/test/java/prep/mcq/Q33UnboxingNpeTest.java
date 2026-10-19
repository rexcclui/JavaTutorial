package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 33 - auto-unboxing null
 *
 *     Map<String, Integer> stock = new HashMap<>();
 *     int count = stock.get("apples");
 *
 * What happens?
 *
 *   A. A NullPointerException is thrown.
 *   B. count is 0.
 *   C. It does not compile: Integer cannot be assigned to int.
 *   D. count is null.
 *
 * ANSWER: A
 *
 * WHY:
 *   get() returns null for a missing key (an Integer reference). Assigning it to an int makes Java auto-unbox it by calling intValue() on null -> NullPointerException.
 *   B. Java never converts null to 0. C. Auto-unboxing is legal, so it compiles. D. A primitive int can never be null.
 *
 * Safe versions: stock.getOrDefault("apples", 0), or check containsKey, or use Integer (not int) for the variable.
 *
 * See it run:  ./run.sh mcq Q33
 */
class Q33UnboxingNpeTest {

    // PURPOSE: Demo - the exact snippet, then the safe alternatives.
    @Test
    void demo_theNpeAndTheFixes() {
        Map<String, Integer> stock = new HashMap<>();
        McqSupport.show("stock.get(\"apples\") = %s   (missing key -> null)", stock.get("apples"));
        McqSupport.attempt("int count = stock.get(\"apples\")", () -> { int count = stock.get("apples"); });
        McqSupport.show("stock.getOrDefault(\"apples\", 0) = %d", stock.getOrDefault("apples", 0));
        Integer boxed = stock.get("apples");
        McqSupport.show("Integer boxed = stock.get(\"apples\") -> %s   (a reference may be null)", boxed);
        assertThat(stock.getOrDefault("apples", 0)).isEqualTo(0);
        assertThat(boxed).isNull();
    }

    // PURPOSE: Option A is CORRECT - the NullPointerException happens at the unboxing.
    @Test
    void optionA_correct_nullPointerException() {
        Map<String, Integer> stock = new HashMap<>();
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> { int count = stock.get("apples"); })
                .isInstanceOf(NullPointerException.class);
    }

    // PURPOSE: Option C is WRONG - it compiles (real compiler); the failure is at runtime.
    @Test
    void optionC_wrong_compiles() {
        String source = """
                import java.util.*;
                public class Snippet {
                    void m() { Map<String, Integer> stock = new HashMap<>(); int count = stock.get("apples"); }
                }
                """;
        var errors = McqSupport.compileErrors("Snippet", source);
        McqSupport.show("compiler errors: %s", errors.isEmpty() ? "none - it compiles" : errors);
        assertThat(errors).isEmpty();
    }

    // PURPOSE: Related trap - the ternary operator unboxes too: mixing Integer and int makes the whole expression an int.
    @Test
    void relatedTrap_ternaryUnboxing() {
        Integer maybeNull = null;
        boolean flag = true;
        McqSupport.attempt("flag ? maybeNull : 0   (types Integer, int)", () -> { Integer r = flag ? maybeNull : 0; });
        Integer safe = flag ? maybeNull : Integer.valueOf(0);          // both operands Integer: no unboxing
        McqSupport.show("flag ? maybeNull : Integer.valueOf(0) = %s   (no unboxing, no NPE)", safe);
        assertThat(safe).isNull();
    }
}
