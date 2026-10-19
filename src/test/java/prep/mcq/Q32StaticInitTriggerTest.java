package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 32 - when does a static initializer run?
 *
 *   A. At JVM startup, for every class in the program.
 *   B. As soon as the class name appears in the source code.
 *   C. Lazily, the first time the class is actively used: creating an instance, calling a static method, or reading/writing a non-constant static field.
 *   D. Every time an object of the class is created.
 *
 * ANSWER: C
 *
 * WHY:
 *   A, B. Classes are loaded and initialized on demand, not up front.
 *   C. Correct. Notice that reading a compile-time CONSTANT (static final int X = 5) does NOT trigger it, because the compiler copies the value into the caller.
 *   D. That describes an instance initializer block / constructor. A static block runs once per class.
 *
 * See it run:  ./run.sh mcq Q32
 */
class Q32StaticInitTriggerTest {
    static final List<String> LOG = new ArrayList<>();

    static class UsedByStaticMethod {
        static { LOG.add("UsedByStaticMethod initialized"); }
        static void touch() { }
    }

    static class UsedByInstance {
        static { LOG.add("UsedByInstance initialized"); }
    }

    static class WithConstant {
        static final int CONST = 5;                               // compile-time constant: inlined by the compiler
        static { LOG.add("WithConstant initialized"); }
    }

    static class WithNonConstant {
        static final int VALUE = Integer.parseInt("5");           // computed at run time: NOT a constant
        static { LOG.add("WithNonConstant initialized"); }
    }

    static class OnlyMentioned {
        static { LOG.add("OnlyMentioned initialized"); }
    }

    static class Counter {
        static int initCount;
        static { initCount++; }
    }

    // PURPOSE: Demo - a log shows exactly WHEN each static block runs relative to the statements around it.
    @Test
    void demo_timeline() {
        LOG.clear();
        LOG.add("1. before touching UsedByStaticMethod");
        UsedByStaticMethod.touch();
        LOG.add("2. after touching it");
        LOG.forEach(line -> McqSupport.show("  %s", line));
        assertThat(LOG).containsExactly("1. before touching UsedByStaticMethod", "UsedByStaticMethod initialized", "2. after touching it");
    }

    // PURPOSE: Option B is WRONG - merely mentioning a class (a variable type, a class literal) does not initialize it.
    @Test
    void optionB_wrong_mentioningIsNotUsing() {
        LOG.clear();
        OnlyMentioned unused = null;                              // type used in a declaration
        Class<?> literal = OnlyMentioned.class;                   // class literal: loads the class but does not initialize it
        McqSupport.show("after mentioning OnlyMentioned: log = %s", LOG);
        assertThat(LOG).doesNotContain("OnlyMentioned initialized");
        assertThat(literal).isNotNull();
        assertThat(unused).isNull();
    }

    // PURPOSE: Option C is CORRECT (part 1) - creating an instance triggers it, the second instance does not run it again.
    @Test
    void optionC_correct_instanceCreationTriggersOnce() {
        LOG.clear();
        new UsedByInstance();
        new UsedByInstance();
        McqSupport.show("after two `new UsedByInstance()`: log = %s", LOG);
        assertThat(LOG).containsExactly("UsedByInstance initialized");
    }

    // PURPOSE: Option C is CORRECT (part 2) - a compile-time constant does NOT trigger initialization, a run-time static final does.
    @Test
    void optionC_correct_constantsDoNotTrigger() {
        LOG.clear();
        int c = WithConstant.CONST;
        McqSupport.show("read WithConstant.CONST = %d      -> log = %s", c, LOG);
        assertThat(LOG).isEmpty();
        int v = WithNonConstant.VALUE;
        McqSupport.show("read WithNonConstant.VALUE = %d   -> log = %s", v, LOG);
        assertThat(LOG).containsExactly("WithNonConstant initialized");
    }

    // PURPOSE: Option D is WRONG - the static block ran once however many objects are created (compare with a per-object initializer).
    @Test
    void optionD_wrong_staticRunsOncePerClass() {
        new Counter();
        new Counter();
        new Counter();
        McqSupport.show("3 objects created, static block ran %d time(s)", Counter.initCount);
        assertThat(Counter.initCount).isEqualTo(1);
    }
}
