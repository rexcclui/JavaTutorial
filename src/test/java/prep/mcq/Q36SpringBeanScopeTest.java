package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * QUESTION 36 - default bean scope in Spring
 *
 * What is the default scope of a Spring bean?
 *
 *   A. prototype: a new instance every time the bean is requested.
 *   B. singleton: one shared instance per Spring container (ApplicationContext).
 *   C. request: one instance per HTTP request.
 *   D. A new instance for every injection point.
 *
 * ANSWER: B
 *
 * WHY:
 *   A. Wrong - prototype must be requested explicitly with @Scope("prototype").
 *   B. Correct - "singleton" in Spring means once per container, NOT once per JVM (two containers each have their own).
 *   C. Wrong - request/session scopes exist only in a web application context.
 *   D. Wrong - every injection point receives the same shared instance.
 *
 * See it run:  ./run.sh mcq Q36
 */
class Q36SpringBeanScopeTest {
    @Component static class Single { }
    @Component @Scope("prototype") static class Proto { }
    @Component static class HolderOne { @Autowired Single single; }
    @Component static class HolderTwo { @Autowired Single single; }
    @Component @Scope("request") static class RequestScoped { }

    // PURPOSE: Demo - ask the container twice for a default bean and twice for a prototype bean; print the object identities.
    @Test
    void demo_singletonVersusPrototype() {
        try (var ctx = new AnnotationConfigApplicationContext(Single.class, Proto.class)) {
            Single s1 = ctx.getBean(Single.class), s2 = ctx.getBean(Single.class);
            Proto p1 = ctx.getBean(Proto.class), p2 = ctx.getBean(Proto.class);
            McqSupport.show("default scope : getBean twice -> identity %d and %d   same object? %s", System.identityHashCode(s1), System.identityHashCode(s2), s1 == s2);
            McqSupport.show("prototype     : getBean twice -> identity %d and %d   same object? %s", System.identityHashCode(p1), System.identityHashCode(p2), p1 == p2);
            assertThat(s1).isSameAs(s2);
            assertThat(p1).isNotSameAs(p2);
        }
    }

    // PURPOSE: Option A is WRONG - without @Scope the bean is shared, not created per request.
    @Test
    void optionA_wrong_defaultIsNotPrototype() {
        try (var ctx = new AnnotationConfigApplicationContext(Single.class)) {
            McqSupport.show("scope of the bean definition: '%s'", ctx.getBeanFactory().getBeanDefinition("q36SpringBeanScopeTest.Single").getScope().isEmpty() ? "(empty = singleton)" : "other");
            assertThat(ctx.getBean(Single.class)).isSameAs(ctx.getBean(Single.class));
            assertThat(ctx.isSingleton("q36SpringBeanScopeTest.Single")).isTrue();
        }
    }

    // PURPOSE: Option B is CORRECT - "singleton" is per container: two containers hold two different instances.
    @Test
    void optionB_correct_singletonIsPerContainer() {
        try (var ctx1 = new AnnotationConfigApplicationContext(Single.class);
             var ctx2 = new AnnotationConfigApplicationContext(Single.class)) {
            McqSupport.show("container 1 bean identity = %d", System.identityHashCode(ctx1.getBean(Single.class)));
            McqSupport.show("container 2 bean identity = %d   <- different: one per container, not per JVM", System.identityHashCode(ctx2.getBean(Single.class)));
            assertThat(ctx1.getBean(Single.class)).isNotSameAs(ctx2.getBean(Single.class));
        }
    }

    // PURPOSE: Option C is WRONG for a plain container - request scope only exists in web applications.
    @Test
    void optionC_wrong_requestScopeNeedsAWebContext() {
        try (var ctx = new AnnotationConfigApplicationContext(RequestScoped.class)) {
            McqSupport.attempt("ctx.getBean(RequestScoped.class)", () -> ctx.getBean(RequestScoped.class));
            assertThatThrownBy(() -> ctx.getBean(RequestScoped.class)).hasMessageContaining("No Scope registered for scope name 'request'");
        }
    }

    // PURPOSE: Option D is WRONG - two different beans that inject Single both receive the very same instance.
    @Test
    void optionD_wrong_injectionPointsShareTheInstance() {
        try (var ctx = new AnnotationConfigApplicationContext(Single.class, HolderOne.class, HolderTwo.class)) {
            Single one = ctx.getBean(HolderOne.class).single, two = ctx.getBean(HolderTwo.class).single;
            McqSupport.show("HolderOne got identity %d, HolderTwo got identity %d", System.identityHashCode(one), System.identityHashCode(two));
            assertThat(one).isSameAs(two);
        }
    }
}
