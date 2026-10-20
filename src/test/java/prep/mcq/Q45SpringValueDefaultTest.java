package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

/**
 * QUESTION 45 - @Value defaults
 *
 *     @Value("${missing.prop:fallback}") String value;      // the property "missing.prop" is not defined anywhere
 *
 * What does value contain?
 *
 *   A. null
 *   B. The application fails to start with an exception.
 *   C. "fallback" - the text after the colon is the default.
 *   D. An empty string.
 *
 * ANSWER: C
 *
 * WHY:
 *   In a ${name:default} placeholder, everything after the first colon is the default used when the property is missing.
 *   A placeholder WITHOUT a default (${missing.prop}) is the one that fails at startup ("Could not resolve placeholder") - that is the situation behind B.
 *   For an empty default write ${missing.prop:}. For a nullable value use @Value("${x:#{null}}").
 *
 * See it run:  ./run.sh mcq Q45
 */
class Q45SpringValueDefaultTest {
    @Configuration static class Placeholders {
        @Bean static PropertySourcesPlaceholderConfigurer configurer() { return new PropertySourcesPlaceholderConfigurer(); }
    }

    @Component static class Holder {
        @Value("${app.name}") String name;                         // defined
        @Value("${missing.prop:fallback}") String withDefault;     // not defined, has a default
        @Value("${missing.prop:}") String emptyDefault;            // not defined, empty default
        @Value("${missing.prop:#{null}}") String nullDefault;      // not defined, null default via SpEL
    }

    @Component static class NoDefault {
        @Value("${absolutely.missing}") String value;
    }

    private static AnnotationConfigApplicationContext context(Class<?>... classes) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of("app.name", "demo")));
        ctx.register(classes);
        return ctx;
    }

    // PURPOSE: Demo - a defined property, a missing one with a default, with an empty default and with a null default.
    @Test
    void demo_valuesInjected() {
        try (var ctx = context(Placeholders.class, Holder.class)) {
            ctx.refresh();
            Holder h = ctx.getBean(Holder.class);
            McqSupport.show("${app.name}                 -> \"%s\"", h.name);
            McqSupport.show("${missing.prop:fallback}    -> \"%s\"", h.withDefault);
            McqSupport.show("${missing.prop:}            -> \"%s\"  (empty string)", h.emptyDefault);
            McqSupport.show("${missing.prop:#{null}}     -> %s", h.nullDefault);
            assertThat(h.name).isEqualTo("demo");
            assertThat(h.withDefault).isEqualTo("fallback");
            assertThat(h.emptyDefault).isEmpty();
            assertThat(h.nullDefault).isNull();
        }
    }

    // PURPOSE: Option C is CORRECT - and an existing property always wins over the default.
    @Test
    void optionC_correct_propertyBeatsDefault() {
        try (var ctx = context(Placeholders.class, Holder.class)) {
            ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource("override", Map.of("missing.prop", "real value")));
            ctx.refresh();
            McqSupport.show("once missing.prop is defined, the value is \"%s\"", ctx.getBean(Holder.class).withDefault);
            assertThat(ctx.getBean(Holder.class).withDefault).isEqualTo("real value");
        }
    }

    // PURPOSE: Option B describes a placeholder WITHOUT a default: startup fails with "Could not resolve placeholder".
    @Test
    void optionB_wrongHere_butTrueWithoutADefault() {
        var ctx = context(Placeholders.class, NoDefault.class);
        try {
            ctx.refresh();
        } catch (BeansException e) {
            Throwable root = NestedExceptionUtils.getRootCause(e);
            McqSupport.show("without a default: %s: %s", root.getClass().getSimpleName(), root.getMessage());
            assertThat(root.getMessage()).contains("Could not resolve placeholder 'absolutely.missing'");
        }
        assertThat(ctx.isActive()).isFalse();
    }

    // PURPOSE: Options A and D are WRONG - the value is the text after the colon, neither null nor empty (unless you write it that way).
    @Test
    void optionsAD_wrong() {
        try (var ctx = context(Placeholders.class, Holder.class)) {
            ctx.refresh();
            assertThat(ctx.getBean(Holder.class).withDefault).isNotNull().isNotEmpty();
        }
    }
}
