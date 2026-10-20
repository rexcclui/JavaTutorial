package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * QUESTION 43 - @Component, @Service, @Repository, @Controller
 *
 * How are these annotations related?
 *
 *   A. @Service, @Repository and @Controller are specializations of @Component (meta-annotated with it), so component scanning detects all of them; they mainly document the layer
 *      (and @Repository additionally enables persistence exception translation).
 *   B. They are four unrelated annotations; scanning only picks up @Component.
 *   C. @Service automatically wraps every method in a transaction.
 *   D. @Controller beans must be prototype scoped.
 *
 * ANSWER: A
 *
 * WHY:
 *   B. Wrong - each stereotype is itself annotated with @Component.   C. Wrong - transactions need @Transactional (and @EnableTransactionManagement).
 *   D. Wrong - they are singletons like any other bean unless you say otherwise.
 *   Bonus: @RestController = @Controller + @ResponseBody.
 *
 * See it run:  ./run.sh mcq Q43
 */
class Q43SpringStereotypesTest {
    @Service static class MyService { }
    @Repository static class MyRepository { }
    @Controller static class MyController { }

    // PURPOSE: Demo - ask Spring's annotation utilities whether each stereotype is a @Component, and whether registering them creates beans.
    @Test
    void demo_everyStereotypeIsAComponent() {
        Class<?>[] stereotypes = {Service.class, Repository.class, Controller.class, RestController.class};
        for (Class<?> a : stereotypes) {
            boolean isComponent = AnnotatedElementUtils.hasAnnotation(a, Component.class);
            McqSupport.show("@%-15s is meta-annotated with @Component? %s", a.getSimpleName(), isComponent);
            assertThat(isComponent).isTrue();
        }
        try (var ctx = new AnnotationConfigApplicationContext(MyService.class, MyRepository.class, MyController.class)) {
            McqSupport.show("beans created: @Service=%d, @Repository=%d, @Controller=%d",
                    ctx.getBeansOfType(MyService.class).size(), ctx.getBeansOfType(MyRepository.class).size(), ctx.getBeansOfType(MyController.class).size());
            assertThat(ctx.getBeansOfType(MyService.class)).hasSize(1);
            assertThat(ctx.getBeansOfType(MyRepository.class)).hasSize(1);
            assertThat(ctx.getBeansOfType(MyController.class)).hasSize(1);
        }
    }

    // PURPOSE: Option C is WRONG - @Service adds no transaction handling; the bean is not even proxied.
    @Test
    void optionC_wrong_serviceAddsNoTransactions() {
        try (var ctx = new AnnotationConfigApplicationContext(MyService.class)) {
            McqSupport.show("is the @Service bean a proxy? %s", AopUtils.isAopProxy(ctx.getBean(MyService.class)));
            assertThat(AopUtils.isAopProxy(ctx.getBean(MyService.class))).isFalse();
        }
    }

    // PURPOSE: Option D is WRONG - a @Controller bean is a normal singleton.
    @Test
    void optionD_wrong_controllersAreSingletons() {
        try (var ctx = new AnnotationConfigApplicationContext(MyController.class)) {
            assertThat(ctx.getBean(MyController.class)).isSameAs(ctx.getBean(MyController.class));
        }
    }

    // PURPOSE: Bonus - @RestController is @Controller plus @ResponseBody (return values become the response body).
    @Test
    void bonus_restControllerIsControllerPlusResponseBody() {
        McqSupport.show("@RestController meta-annotated with @Controller? %s, with @ResponseBody? %s",
                AnnotatedElementUtils.hasAnnotation(RestController.class, Controller.class),
                AnnotatedElementUtils.hasAnnotation(RestController.class, ResponseBody.class));
        assertThat(AnnotatedElementUtils.hasAnnotation(RestController.class, Controller.class)).isTrue();
        assertThat(AnnotatedElementUtils.hasAnnotation(RestController.class, ResponseBody.class)).isTrue();
    }
}
