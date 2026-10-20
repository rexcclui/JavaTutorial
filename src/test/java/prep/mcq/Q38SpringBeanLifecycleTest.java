package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * QUESTION 38 - bean lifecycle callbacks
 *
 * For a singleton bean with a constructor, a setter marked @Autowired, a @PostConstruct method and a @PreDestroy method,
 * which is the order of events?
 *
 *   A. @PostConstruct, constructor, setter injection, ... , @PreDestroy
 *   B. setter injection, constructor, @PostConstruct, ... , @PreDestroy
 *   C. constructor, @PostConstruct, setter injection, ... , @PreDestroy
 *   D. constructor, setter injection, @PostConstruct, ... (bean in use) ..., @PreDestroy when the context is closed
 *
 * ANSWER: D
 *
 * WHY:
 *   Spring must create the object before it can inject into it (so constructor first), and @PostConstruct runs after ALL dependencies are injected,
 *   so you can safely use them there. @PreDestroy runs when the container shuts down (ctx.close()), once per singleton.
 *
 * See it run:  ./run.sh mcq Q38
 */
class Q38SpringBeanLifecycleTest {
    static final List<String> LOG = new ArrayList<>();

    @Component static class Dep { }

    @Component static class Lifecycle {
        Lifecycle() { LOG.add("1. constructor"); }
        @Autowired void setDep(Dep dep) { LOG.add("2. setter injection (dependency available: " + (dep != null) + ")"); }
        @PostConstruct void init() { LOG.add("3. @PostConstruct"); }
        @PreDestroy void cleanup() { LOG.add("5. @PreDestroy"); }
    }

    // PURPOSE: Demo - start the context, use the bean, close the context; print every event in order.
    @Test
    void demo_orderOfEvents() {
        LOG.clear();
        var ctx = new AnnotationConfigApplicationContext(Dep.class, Lifecycle.class);
        LOG.add("4. context started, bean in use");
        ctx.close();
        LOG.forEach(line -> McqSupport.show("  %s", line));
        assertThat(LOG).containsExactly(
                "1. constructor",
                "2. setter injection (dependency available: true)",
                "3. @PostConstruct",
                "4. context started, bean in use",
                "5. @PreDestroy");
    }

    // PURPOSE: Options A, B, C are WRONG - each puts one step in an impossible place (e.g. @PostConstruct cannot run before the object exists).
    @Test
    void optionsABC_wrong_impossibleOrders() {
        LOG.clear();
        try (var ctx = new AnnotationConfigApplicationContext(Dep.class, Lifecycle.class)) {
            assertThat(LOG.indexOf("1. constructor")).isLessThan(LOG.indexOf("3. @PostConstruct"));
            assertThat(LOG.indexOf("2. setter injection (dependency available: true)")).isLessThan(LOG.indexOf("3. @PostConstruct"));
            McqSupport.show("constructor < setter injection < @PostConstruct, as option D says");
        }
    }

    static final List<Boolean> SEEN = new ArrayList<>();

    @Component static class FieldInjected {
        @Autowired Dep dep;
        FieldInjected() { SEEN.add(dep != null); }
        @PostConstruct void init() { SEEN.add(dep != null); }
    }

    // PURPOSE: Why @PostConstruct exists - the constructor runs BEFORE injection, so a constructor cannot use field-injected dependencies; @PostConstruct can.
    @Test
    void why_constructorCannotUseFieldInjectedDependencies() {
        SEEN.clear();
        try (var ctx = new AnnotationConfigApplicationContext(Dep.class, FieldInjected.class)) {
            McqSupport.show("dependency visible in constructor? %s,  in @PostConstruct? %s", SEEN.get(0), SEEN.get(1));
            assertThat(SEEN).containsExactly(false, true);
        }
    }
}
