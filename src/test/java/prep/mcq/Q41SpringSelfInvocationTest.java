package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * QUESTION 41 - proxies and self-invocation (the famous @Transactional trap)
 *
 *     @Service class Service {
 *         @Transactional public void outer() { inner(); }       // plain this.inner() call
 *         @Transactional public void inner() { ... }
 *     }
 *
 * A client calls service.outer(). Is the proxy advice (here: the transaction) applied to the inner() call?
 *
 *   A. Yes, always; Spring advises every annotated method call.
 *   B. Yes, but only if inner() is public.
 *   C. No: the call goes through `this`, directly to the target object, bypassing the proxy, so no advice is applied.
 *   D. Yes, but only for prototype-scoped beans.
 *
 * ANSWER: C
 *
 * WHY:
 *   Spring AOP (and @Transactional, @Async, @Cacheable...) works by wrapping the bean in a PROXY. Calls from OTHER beans go through the proxy and get the advice.
 *   A call from inside the bean uses `this`, which is the raw target object, so the proxy never sees it.
 *   Fixes: move inner() to another bean, inject the bean into itself (@Lazy self-reference), call it through AopContext.currentProxy(), or use AspectJ weaving.
 *   (This demo uses a custom @Audited aspect instead of @Transactional so it needs no database; the mechanism is identical.)
 *
 * See it run:  ./run.sh mcq Q41
 */
class Q41SpringSelfInvocationTest {
    static final List<String> ADVISED = new ArrayList<>();

    @Aspect
    @Component
    static class AuditAspect {
        @Around("@annotation(prep.mcq.Audited)")
        public Object around(ProceedingJoinPoint pjp) throws Throwable {
            ADVISED.add(pjp.getSignature().getName());
            return pjp.proceed();
        }
    }

    @Component
    static class Service {
        @Autowired @Lazy Service self;

        @Audited public String outer() { return "outer>" + inner(); }            // this.inner(): bypasses the proxy
        @Audited public String inner() { return "inner"; }
        @Audited public String outerViaSelf() { return "outer>" + self.inner(); } // goes through the proxy
    }

    @Configuration
    @EnableAspectJAutoProxy
    static class Cfg { }

    private static AnnotationConfigApplicationContext context() {
        ADVISED.clear();
        return new AnnotationConfigApplicationContext(Cfg.class, AuditAspect.class, Service.class);
    }

    // PURPOSE: Demo - call outer() from outside and print which methods the proxy advised.
    @Test
    void demo_whichCallsGetAdvice() {
        try (var ctx = context()) {
            Service service = ctx.getBean(Service.class);
            McqSupport.show("bean class: %s   (a CGLIB proxy subclass)", service.getClass().getSimpleName());
            service.outer();
            McqSupport.show("client calls outer()  -> advised methods: %s   <- inner() was NOT advised", ADVISED);
            assertThat(ADVISED).containsExactly("outer");
        }
    }

    // PURPOSE: Option C is CORRECT - the inner call missed the proxy. A call to inner() FROM OUTSIDE is advised normally.
    @Test
    void optionC_correct_externalCallIsAdvisedInternalIsNot() {
        try (var ctx = context()) {
            Service service = ctx.getBean(Service.class);
            service.inner();
            McqSupport.show("client calls inner() directly -> advised methods: %s", ADVISED);
            assertThat(ADVISED).containsExactly("inner");
        }
    }

    // PURPOSE: Options A, B, D are WRONG - visibility and scope do not matter; the only thing that matters is whether the call passes through the proxy object.
    @Test
    void optionsABD_wrong_onlyThePathMatters() {
        try (var ctx = context()) {
            Service service = ctx.getBean(Service.class);
            assertThat(AopUtils.isCglibProxy(service)).isTrue();
            service.outer();
            assertThat(ADVISED).doesNotContain("inner");        // inner() is public and the bean is a singleton, still not advised
            McqSupport.show("inner() is public and the bean is a singleton, yet it was not advised");
        }
    }

    // PURPOSE: Fix - call inner() through a reference to the PROXY (self-injection) and the advice is applied.
    @Test
    void fix_callThroughTheProxy() {
        try (var ctx = context()) {
            ctx.getBean(Service.class).outerViaSelf();
            McqSupport.show("outerViaSelf() -> advised methods: %s   <- inner() now advised too", ADVISED);
            assertThat(ADVISED).containsExactly("outerViaSelf", "inner");
        }
    }
}
