package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import prep.mcq.sample.OrderService;
import prep.mcq.sample.SampleConfig;

/**
 * QUESTION 44 - default bean names
 *
 *     @Component class OrderService { }                       // no explicit name
 *     @Configuration class SampleConfig { @Bean String makeInvoice() { ... } }
 *
 * What bean names does Spring give them?
 *
 *   A. "OrderService" and "invoice"
 *   B. "orderService" and "makeInvoice"
 *   C. "orderService" and "invoice"
 *   D. The fully qualified class name and the fully qualified method name
 *
 * ANSWER: B
 *
 * WHY:
 *   @Component (and the stereotypes): the simple class name with the first letter lower-cased.   @Bean: the NAME OF THE METHOD.
 *   Both can be overridden: @Component("x") or @Bean("x") / @Bean(name = {"x", "alias"}).
 *   (The classes live in prep.mcq.sample so they are top-level; a nested class would get a name like "outer.Inner".)
 *
 * See it run:  ./run.sh mcq Q44
 */
class Q44SpringBeanNamesTest {

    // PURPOSE: Demo - print every bean name Spring registered for the sample classes.
    @Test
    void demo_theRegisteredNames() {
        try (var ctx = new AnnotationConfigApplicationContext(OrderService.class, SampleConfig.class)) {
            String[] names = Arrays.stream(ctx.getBeanDefinitionNames()).filter(n -> !n.startsWith("org.springframework")).sorted().toArray(String[]::new);
            McqSupport.show("bean names: %s", Arrays.toString(names));
            assertThat(names).contains("orderService", "makeInvoice", "custom", "sampleConfig");
        }
    }

    // PURPOSE: Option B is CORRECT - look the beans up by those exact names.
    @Test
    void optionB_correct_lookUpByName() {
        try (var ctx = new AnnotationConfigApplicationContext(OrderService.class, SampleConfig.class)) {
            assertThat(ctx.getBean("orderService")).isInstanceOf(OrderService.class);
            assertThat(ctx.getBean("makeInvoice")).isEqualTo("invoice");
            McqSupport.show("ctx.getBean(\"makeInvoice\") = %s", ctx.getBean("makeInvoice"));
        }
    }

    // PURPOSE: Options A, C, D are WRONG - those names do not exist in the container.
    @Test
    void optionsACD_wrong_namesDoNotExist() {
        try (var ctx = new AnnotationConfigApplicationContext(OrderService.class, SampleConfig.class)) {
            McqSupport.show("containsBean(\"OrderService\") = %s", ctx.containsBean("OrderService"));
            McqSupport.show("containsBean(\"invoice\")      = %s", ctx.containsBean("invoice"));
            McqSupport.show("containsBean(\"prep.mcq.sample.OrderService\") = %s", ctx.containsBean("prep.mcq.sample.OrderService"));
            assertThat(ctx.containsBean("OrderService")).isFalse();
            assertThat(ctx.containsBean("invoice")).isFalse();
            assertThat(ctx.containsBean("prep.mcq.sample.OrderService")).isFalse();
        }
    }

    // PURPOSE: Overriding - an explicit @Bean("custom") replaces the method name (the method second() has no bean called "second").
    @Test
    void overriding_explicitNameReplacesTheMethodName() {
        try (var ctx = new AnnotationConfigApplicationContext(SampleConfig.class)) {
            assertThat(ctx.containsBean("custom")).isTrue();
            assertThat(ctx.containsBean("second")).isFalse();
            McqSupport.show("@Bean(\"custom\") on method second(): has 'custom'? %s, has 'second'? %s", ctx.containsBean("custom"), ctx.containsBean("second"));
        }
    }
}
