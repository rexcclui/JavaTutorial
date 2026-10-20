package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;

/**
 * QUESTION 42 - circular dependencies
 *
 *     @Component class A { A(B b) { } }
 *     @Component class B { B(A a) { } }
 *
 * What happens when the application context starts?
 *
 *   A. Spring automatically injects a lazy proxy for one of them.
 *   B. Spring injects null for one of them.
 *   C. It works, because both are singletons.
 *   D. It fails with BeanCurrentlyInCreationException (a circular reference), reported inside an UnsatisfiedDependencyException.
 *
 * ANSWER: D
 *
 * WHY:
 *   With CONSTRUCTOR injection, A cannot be created until B exists and B cannot be created until A exists: a deadlock Spring cannot break.
 *   Plain Spring Framework can resolve cycles between FIELD / SETTER injected singletons by exposing a half-built early reference, but Spring Boot 2.6+ turns that off
 *   (spring.main.allow-circular-references=false) because cycles are a design smell. Best fix: break the cycle (extract a third bean). Quick fix: @Lazy on one parameter.
 *
 * See it run:  ./run.sh mcq Q42
 */
class Q42SpringCircularDependencyTest {
    @Component static class CycleA { CycleA(CycleB b) { } }
    @Component static class CycleB { CycleB(CycleA a) { } }

    @Component static class FieldA { @Autowired FieldB b; }
    @Component static class FieldB { @Autowired FieldA a; }

    @Component static class LazyA { LazyA(@Lazy LazyB b) { } }
    @Component static class LazyB { LazyB(LazyA a) { } }

    // PURPOSE: Demo - start the constructor-cycle context and print the exception chain.
    @Test
    void demo_constructorCycleFails() {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(CycleA.class, CycleB.class);
        try {
            ctx.refresh();
        } catch (BeansException e) {
            McqSupport.show("top exception : %s", e.getClass().getSimpleName());
            McqSupport.show("root cause    : %s", NestedExceptionUtils.getRootCause(e).getClass().getSimpleName());
            McqSupport.show("message       : %s", NestedExceptionUtils.getRootCause(e).getMessage().lines().findFirst().orElse(""));
            assertThat(NestedExceptionUtils.getRootCause(e)).isInstanceOf(BeanCurrentlyInCreationException.class);
        }
    }

    // PURPOSE: Options A, B, C are WRONG for constructor injection - no automatic proxy, no null, and being singletons does not help.
    @Test
    void optionsABC_wrong_noAutomaticRescue() {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(CycleA.class, CycleB.class);
        McqSupport.attempt("context.refresh()", ctx::refresh);
        assertThat(ctx.isActive()).isFalse();
    }

    // PURPOSE: Contrast - plain Spring Framework DOES allow the same cycle with field injection (early references).
    @Test
    void contrast_fieldInjectionCycleWorksInPlainSpring() {
        try (var ctx = new AnnotationConfigApplicationContext(FieldA.class, FieldB.class)) {
            McqSupport.show("field-injected cycle: A.b = %s, B.a = %s", ctx.getBean(FieldA.class).b != null, ctx.getBean(FieldB.class).a != null);
            assertThat(ctx.getBean(FieldA.class).b).isSameAs(ctx.getBean(FieldB.class));
        }
    }

    // PURPOSE: Contrast - with circular references disabled (the Spring Boot 2.6+ default) even the field-injected cycle fails.
    @Test
    void contrast_springBootDefaultDisablesCycles() {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.setAllowCircularReferences(false);
        ctx.register(FieldA.class, FieldB.class);
        McqSupport.attempt("refresh with allowCircularReferences = false", ctx::refresh);
        assertThat(ctx.isActive()).isFalse();
    }

    // PURPOSE: Quick fix - @Lazy on one constructor parameter injects a proxy and breaks the cycle at creation time.
    @Test
    void fix_lazyOnOneParameter() {
        try (var ctx = new AnnotationConfigApplicationContext(LazyA.class, LazyB.class)) {
            McqSupport.show("with @Lazy on one parameter the context starts: %s", ctx.isActive());
            assertThat(ctx.isActive()).isTrue();
        }
    }
}
