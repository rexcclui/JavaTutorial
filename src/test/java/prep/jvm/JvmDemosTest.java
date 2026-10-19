package prep.jvm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Runnable demos of JVM behaviour that interviewers love. Read each test, predict, then verify. */
class JvmDemosTest {
    // PURPOSE: StackOverflowError extends Error, not Exception, so catch (Exception e) will not stop runaway recursion.
    @Test
    void stackOverflowIsAnError_notAnException() {
        assertThatThrownBy(JvmDemosTest::recurseForever).isInstanceOf(StackOverflowError.class);
        // It's an Error (subclass of VirtualMachineError), so `catch (Exception e)` would NOT catch it.
        assertThat(Error.class.isAssignableFrom(StackOverflowError.class)).isTrue();
    }

    private static int recurseForever() { return 1 + recurseForever(); }

    // PURPOSE: Class/object initialisation order: parent static, child static, then parent init+ctor, child init+ctor for each new object.
    @Test
    void staticInitRunsOnceAndInOrder() {
        List<String> log = Init.LOG;
        log.clear();
        new Init.Child();
        new Init.Child();
        // Parent static -> Child static -> (per instance) Parent init, Parent ctor, Child init, Child ctor
        assertThat(log).containsExactly(
                "parent-static", "child-static",
                "parent-init", "parent-ctor", "child-init", "child-ctor",
                "parent-init", "parent-ctor", "child-init", "child-ctor");
    }

    static class Init {
        static final List<String> LOG = new ArrayList<>();

        static class Parent {
            static { LOG.add("parent-static"); }
            { LOG.add("parent-init"); }
            Parent() { LOG.add("parent-ctor"); }
        }

        static class Child extends Parent {
            static { LOG.add("child-static"); }
            { LOG.add("child-init"); }
            Child() { LOG.add("child-ctor"); }
        }
    }

    // PURPOSE: A weak reference does not keep an object alive, but GC timing is not guaranteed, so tests must not depend on it.
    @Test
    void weakReferenceCanBeCollected() {
        Object strong = new Object();
        WeakReference<Object> weak = new WeakReference<>(strong);
        assertThat(weak.get()).isSameAs(strong);   // strongly reachable -> never cleared
        // After `strong = null; System.gc();` weak.get() MAY become null -- GC timing is not guaranteed,
        // so never write a test that depends on it. That non-determinism is the interview point.
    }

    // PURPOSE: Long.equals(Integer) is false even for the same number: equals() also compares the type.
    @Test
    void equalsOnStringsAndBoxedTypes() {
        assertThat(Long.valueOf(5).equals(5)).isFalse();      // Long vs Integer: different types -> false
        assertThat(Long.valueOf(5).equals(5L)).isTrue();
    }

    // PURPOSE: String methods return new strings; the original never changes, which is why Strings are safe to share.
    @Test
    void immutableStringsAreSafeToShare() {
        String a = "abc";
        String b = a.toUpperCase();
        assertThat(a).isEqualTo("abc");   // methods return NEW strings
        assertThat(b).isEqualTo("ABC");
    }
}
