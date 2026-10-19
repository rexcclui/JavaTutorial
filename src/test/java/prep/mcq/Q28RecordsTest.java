package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 28 - records
 *
 *     record Point(int x, int y) { }
 *
 * Which statement is true?
 *
 *   A. The fields are mutable and setX / setY are generated.
 *   B. A record cannot declare any methods.
 *   C. The compiler generates private final fields, accessors x() and y(), a canonical constructor, and equals/hashCode/toString.
 *   D. A record can extend another class.
 *
 * ANSWER: C
 *
 * WHY:
 *   A. Wrong - records are shallowly immutable; there are no setters.
 *   B. Wrong - you can add instance methods, static methods and extra constructors.
 *   C. Correct.
 *   D. Wrong - a record implicitly extends java.lang.Record and cannot extend anything else (it can implement interfaces).
 *
 * "Shallowly" immutable: a record holding a List can still have that list's contents changed unless you copy it.
 *
 * See it run:  ./run.sh mcq Q28
 */
class Q28RecordsTest {
    record Point(int x, int y) {
        double distanceFromOrigin() { return Math.sqrt(x * x + y * y); }     // extra methods are fine
    }

    // PURPOSE: Demo - print what the compiler generated for the record: fields, methods, toString, equals.
    @Test
    void demo_whatTheCompilerGenerated() {
        McqSupport.show("fields : %s", Arrays.stream(Point.class.getDeclaredFields()).map(f -> java.lang.reflect.Modifier.toString(f.getModifiers()) + " " + f.getName()).toList());
        McqSupport.show("toString   : %s", new Point(3, 4));
        McqSupport.show("equals     : new Point(3,4).equals(new Point(3,4)) = %s", new Point(3, 4).equals(new Point(3, 4)));
        McqSupport.show("accessors  : x() = %d, y() = %d", new Point(3, 4).x(), new Point(3, 4).y());
        McqSupport.show("own method : distanceFromOrigin() = %.1f", new Point(3, 4).distanceFromOrigin());
        assertThat(new Point(3, 4)).isEqualTo(new Point(3, 4)).hasToString("Point[x=3, y=4]");
        assertThat(new Point(3, 4).hashCode()).isEqualTo(new Point(3, 4).hashCode());
    }

    // PURPOSE: Option A is WRONG - fields are private final and there is no setter method.
    @Test
    void optionA_wrong_fieldsAreFinalNoSetters() {
        for (var f : Point.class.getDeclaredFields()) {
            assertThat(Modifier.isFinal(f.getModifiers())).isTrue();
            assertThat(Modifier.isPrivate(f.getModifiers())).isTrue();
        }
        var methodNames = Arrays.stream(Point.class.getDeclaredMethods()).map(m -> m.getName()).toList();
        McqSupport.show("declared methods: %s", methodNames);
        assertThat(methodNames).doesNotContain("setX", "setY");
    }

    // PURPOSE: Option B is WRONG - Point declares distanceFromOrigin(); static members and compact constructors are allowed as well.
    @Test
    void optionB_wrong_methodsAreAllowed() {
        assertThat(new Point(6, 8).distanceFromOrigin()).isEqualTo(10.0);
    }

    // PURPOSE: Option C is CORRECT - a compact constructor can validate; accessors are x() not getX().
    @Test
    void optionC_correct_compactConstructorValidates() {
        record Positive(int n) {
            Positive {
                if (n <= 0) throw new IllegalArgumentException("n must be > 0");
            }
        }
        McqSupport.attempt("new Positive(5)", () -> new Positive(5));
        McqSupport.attempt("new Positive(-1)", () -> new Positive(-1));
        assertThat(new Positive(5).n()).isEqualTo(5);
    }

    // PURPOSE: Option D is WRONG - a record cannot extend a class (checked with the real compiler); it can implement interfaces.
    @Test
    void optionD_wrong_cannotExtendAClass() {
        var errors = McqSupport.compileErrors("Snippet", "public class Snippet { static class Base {} record R(int a) extends Base {} }");
        McqSupport.show("record R(int a) extends Base -> %s", errors.isEmpty() ? "compiles" : "ERROR");
        assertThat(errors).isNotEmpty();
        var ok = McqSupport.compileErrors("Snippet", "public class Snippet { interface I {} record R(int a) implements I {} }");
        assertThat(ok).isEmpty();
    }

    // PURPOSE: Caveat - "shallow" immutability: a record holding a mutable List can still change through that list.
    @Test
    void caveat_shallowImmutability() {
        record Team(java.util.List<String> members) {}
        var list = new java.util.ArrayList<>(java.util.List.of("ann"));
        Team team = new Team(list);
        list.add("bob");
        McqSupport.show("record is immutable, but the list inside changed: %s", team);
        assertThat(team.members()).containsExactly("ann", "bob");
    }
}
