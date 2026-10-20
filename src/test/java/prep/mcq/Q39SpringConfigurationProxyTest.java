package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QUESTION 39 - @Bean methods calling each other
 *
 *     @Configuration class Config {
 *         @Bean Dep dep() { return new Dep(); }
 *         @Bean A a() { return new A(dep()); }
 *         @Bean B b() { return new B(dep()); }
 *     }
 *
 * How many Dep objects are created?
 *
 *   A. One: Spring intercepts the dep() calls and returns the shared singleton bean.
 *   B. Two: each call to dep() runs the method body again.
 *   C. Three: one for the context plus one per call.
 *   D. It depends on which bean is created first.
 *
 * ANSWER: A
 *
 * WHY:
 *   A @Configuration class is subclassed with CGLIB; calls to a @Bean method from inside the class are intercepted and answered from the container.
 *   Without that (plain @Component class, or @Configuration(proxyBeanMethods = false)) the method is an ordinary Java call and you get a new Dep each time ("lite mode").
 *
 * See it run:  ./run.sh mcq Q39
 */
class Q39SpringConfigurationProxyTest {
    static final AtomicInteger DEP_CREATED = new AtomicInteger();

    static class Dep { Dep() { DEP_CREATED.incrementAndGet(); } }
    static class A { final Dep dep; A(Dep dep) { this.dep = dep; } }
    static class B { final Dep dep; B(Dep dep) { this.dep = dep; } }

    @Configuration static class FullConfig {
        @Bean Dep dep() { return new Dep(); }
        @Bean A a() { return new A(dep()); }
        @Bean B b() { return new B(dep()); }
    }

    @Configuration(proxyBeanMethods = false) static class LiteConfig {
        @Bean Dep dep() { return new Dep(); }
        @Bean A a() { return new A(dep()); }
        @Bean B b() { return new B(dep()); }
    }

    // PURPOSE: Demo - count how many Dep objects each style creates and whether A and B share one.
    @Test
    void demo_fullVersusLite() {
        DEP_CREATED.set(0);
        try (var ctx = new AnnotationConfigApplicationContext(FullConfig.class)) {
            McqSupport.show("@Configuration                        : Dep objects created = %d, A.dep == B.dep ? %s", DEP_CREATED.get(), ctx.getBean(A.class).dep == ctx.getBean(B.class).dep);
            assertThat(DEP_CREATED.get()).isEqualTo(1);
        }
        DEP_CREATED.set(0);
        try (var ctx = new AnnotationConfigApplicationContext(LiteConfig.class)) {
            McqSupport.show("@Configuration(proxyBeanMethods=false): Dep objects created = %d, A.dep == B.dep ? %s", DEP_CREATED.get(), ctx.getBean(A.class).dep == ctx.getBean(B.class).dep);
            assertThat(DEP_CREATED.get()).isEqualTo(3);        // the @Bean dep() itself + one call from a() + one from b()
        }
    }

    // PURPOSE: Option A is CORRECT - one shared Dep in a normal @Configuration class.
    @Test
    void optionA_correct_oneSharedDep() {
        DEP_CREATED.set(0);
        try (var ctx = new AnnotationConfigApplicationContext(FullConfig.class)) {
            assertThat(ctx.getBean(A.class).dep).isSameAs(ctx.getBean(B.class).dep).isSameAs(ctx.getBean(Dep.class));
            assertThat(DEP_CREATED.get()).isEqualTo(1);
        }
    }

    // PURPOSE: Options B and C describe "lite mode" - which only happens when proxying is switched off; there the beans really are separate objects.
    @Test
    void optionsBC_onlyTrueInLiteMode() {
        try (var ctx = new AnnotationConfigApplicationContext(LiteConfig.class)) {
            assertThat(ctx.getBean(A.class).dep).isNotSameAs(ctx.getBean(B.class).dep);
            McqSupport.show("lite mode: A.dep and B.dep are different objects (and neither is the Dep bean in the container)");
        }
    }

    // PURPOSE: Why it works - the configuration class itself is replaced by a CGLIB subclass (check its class name).
    @Test
    void why_configurationClassIsProxied() {
        try (var ctx = new AnnotationConfigApplicationContext(FullConfig.class)) {
            Object config = ctx.getBean(FullConfig.class);
            McqSupport.show("bean class of the @Configuration: %s", config.getClass().getName());
            assertThat(config.getClass().getName()).contains("$$SpringCGLIB$$");
        }
    }
}
