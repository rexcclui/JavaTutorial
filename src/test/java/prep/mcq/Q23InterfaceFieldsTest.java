package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 23 - fields in an interface
 *
 *     interface Limits { int MAX = 10; }
 *
 * The field MAX is implicitly:
 *
 *   A. private and non-static
 *   B. public static final
 *   C. protected static
 *   D. package-private and final
 *
 * ANSWER: B
 *
 * WHY:
 *   Every field in an interface is a constant: public static final, whether or not you write the modifiers.
 *   (Methods without a body are public abstract; since Java 8 you can also have default and static methods, since 9 private ones.)
 *
 * See it run:  ./run.sh mcq Q23
 */
class Q23InterfaceFieldsTest {
    interface Limits {
        int MAX = 10;
    }

    // PURPOSE: Demo - read the real modifiers of Limits.MAX with reflection.
    @Test
    void demo_modifiersOfTheField() throws Exception {
        Field f = Limits.class.getField("MAX");
        McqSupport.show("modifiers of Limits.MAX = %s", Modifier.toString(f.getModifiers()));
        McqSupport.show("accessed without an object: Limits.MAX = %d", Limits.MAX);
        assertThat(Modifier.isPublic(f.getModifiers())).isTrue();
        assertThat(Modifier.isStatic(f.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(f.getModifiers())).isTrue();
    }

    // PURPOSE: Options A, C and D are WRONG - the field is public, static AND final; check each modifier.
    @Test
    void optionsACD_wrong_eachModifierChecked() throws Exception {
        int m = Limits.class.getField("MAX").getModifiers();
        McqSupport.show("private? %s  protected? %s  package-private (no public/protected/private)? %s",
                Modifier.isPrivate(m), Modifier.isProtected(m), !(Modifier.isPublic(m) || Modifier.isProtected(m) || Modifier.isPrivate(m)));
        assertThat(Modifier.isPrivate(m)).isFalse();
        assertThat(Modifier.isProtected(m)).isFalse();
    }

    // PURPOSE: Because it is final, assigning to it is a compile error (checked with the real compiler).
    @Test
    void consequence_cannotBeReassigned() {
        String source = """
                public class Snippet {
                    interface Limits { int MAX = 10; }
                    void m() { Limits.MAX = 20; }
                }
                """;
        var errors = McqSupport.compileErrors("Snippet", source);
        McqSupport.show("Limits.MAX = 20  ->  %s", errors);
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0)).contains("cannot assign a value to static final variable MAX");
    }

    // PURPOSE: Contrast - a class field with no modifier is a normal package-private instance field (the opposite of an interface constant).
    @Test
    void contrast_classFieldWithNoModifier() throws Exception {
        class Holder { int value = 1; }
        int m = Holder.class.getDeclaredField("value").getModifiers();
        McqSupport.show("class field `int value` modifiers = '%s' (empty = package-private, non-static, non-final)", Modifier.toString(m));
        assertThat(Modifier.isStatic(m)).isFalse();
        assertThat(Modifier.isFinal(m)).isFalse();
    }
}
