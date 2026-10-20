package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * QUESTION 50 - constructor injection and @Autowired
 *
 *     @Component class OrderService {
 *         private final PaymentClient client;
 *         OrderService(PaymentClient client) { this.client = client; }       // no @Autowired anywhere
 *     }
 *
 * Does Spring inject the PaymentClient?
 *
 *   A. Yes. Since Spring 4.3 a class with a SINGLE constructor does not need @Autowired on it.
 *   B. No. @Autowired is always required, otherwise the parameter is null.
 *   C. Only if the bean is defined in XML.
 *   D. Only through a setter method that Spring generates.
 *
 * ANSWER: A
 *
 * WHY:
 *   With one constructor, Spring uses it and resolves each parameter as a bean. With SEVERAL constructors you must mark exactly one with @Autowired,
 *   otherwise Spring looks for a no-argument constructor and fails if there is none.
 *   Constructor injection is the recommended style: dependencies can be final, the object is never half-initialised, and you can create it in a unit test with plain `new`.
 *
 * See it run:  ./run.sh mcq Q50
 */
class Q50SpringSingleConstructorTest {
    @Component static class PaymentClient { }

    @Component static class SingleConstructor {
        final PaymentClient client;
        SingleConstructor(PaymentClient client) { this.client = client; }
    }

    @Component static class TwoConstructorsNoAnnotation {
        final PaymentClient client;
        TwoConstructorsNoAnnotation(PaymentClient client) { this.client = client; }
        TwoConstructorsNoAnnotation(PaymentClient client, String extra) { this.client = client; }
    }

    @Component static class TwoConstructorsAnnotated {
        final PaymentClient client;
        @Autowired TwoConstructorsAnnotated(PaymentClient client) { this.client = client; }
        TwoConstructorsAnnotated(PaymentClient client, String extra) { this.client = client; }
    }

    // PURPOSE: Demo - a single constructor with no @Autowired: the dependency is injected.
    @Test
    void demo_singleConstructorWorks() {
        try (var ctx = new AnnotationConfigApplicationContext(PaymentClient.class, SingleConstructor.class)) {
            McqSupport.show("SingleConstructor.client injected? %s", ctx.getBean(SingleConstructor.class).client != null);
            assertThat(ctx.getBean(SingleConstructor.class).client).isSameAs(ctx.getBean(PaymentClient.class));
        }
    }

    // PURPOSE: Option A is CORRECT - and it is why you rarely see @Autowired on constructors in modern code.
    @Test
    void optionA_correct_noAutowiredNeeded() {
        try (var ctx = new AnnotationConfigApplicationContext(PaymentClient.class, SingleConstructor.class)) {
            assertThat(ctx.getBean(SingleConstructor.class).client).isNotNull();
        }
    }

    // PURPOSE: Option B is WRONG for one constructor - but with TWO constructors and no @Autowired Spring cannot choose and fails (no default constructor).
    @Test
    void optionB_wrong_butTwoConstructorsNeedAutowired() {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(PaymentClient.class, TwoConstructorsNoAnnotation.class);
        try {
            ctx.refresh();
        } catch (Exception e) {
            Throwable root = org.springframework.core.NestedExceptionUtils.getRootCause(e);
            McqSupport.show("two constructors, no @Autowired -> %s: %s", root.getClass().getSimpleName(), root.getMessage().lines().findFirst().orElse(""));
        }
        assertThat(ctx.isActive()).isFalse();
    }

    // PURPOSE: Fix - mark the constructor Spring should use with @Autowired.
    @Test
    void fix_autowiredPicksTheConstructor() {
        try (var ctx = new AnnotationConfigApplicationContext(PaymentClient.class, TwoConstructorsAnnotated.class)) {
            McqSupport.show("with @Autowired on one of two constructors: client injected? %s", ctx.getBean(TwoConstructorsAnnotated.class).client != null);
            assertThat(ctx.getBean(TwoConstructorsAnnotated.class).client).isNotNull();
        }
    }

    // PURPOSE: Bonus - why constructor injection is recommended: a unit test can build the object with plain `new`, no Spring needed.
    @Test
    void bonus_unitTestWithoutSpring() {
        SingleConstructor service = new SingleConstructor(new PaymentClient());
        McqSupport.show("created with plain `new`, no container: client = %s", service.client != null);
        assertThat(service.client).isNotNull();
    }
}
