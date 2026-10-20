package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;

/**
 * QUESTION 37 - two beans of the same type
 *
 *     interface Greeter { }
 *     @Component class English implements Greeter { }
 *     @Component class French implements Greeter { }
 *     @Component class Client { @Autowired Greeter greeter; }
 *
 * What happens when the application context starts?
 *
 *   A. Spring injects the bean whose name comes first alphabetically (English).
 *   B. Spring injects the bean that was registered last (French).
 *   C. It fails with NoUniqueBeanDefinitionException (wrapped in UnsatisfiedDependencyException), unless you disambiguate with @Primary, @Qualifier, or a field/parameter name matching a bean name.
 *   D. Spring injects null.
 *
 * ANSWER: C
 *
 * WHY:
 *   Spring first looks for beans by TYPE. If more than one candidate remains, it tries (1) @Primary, (2) @Qualifier, (3) a bean whose NAME equals the field/parameter name.
 *   If none of those decides, it refuses to guess and fails at startup (fail fast). A, B and D are all "silent guess" behaviours Spring deliberately does not have.
 *
 * See it run:  ./run.sh mcq Q37
 */
class Q37SpringAmbiguousInjectionTest {
    interface Greeter { String greet(); }
    @Component("english") static class English implements Greeter { public String greet() { return "Hello"; } }
    @Component("french") static class French implements Greeter { public String greet() { return "Bonjour"; } }

    @Component static class Ambiguous { @Autowired Greeter greeter; }                     // field name matches no bean name
    @Component static class ByFieldName { @Autowired Greeter french; }                    // field name = bean name "french"
    @Component static class ByQualifier { @Autowired @Qualifier("english") Greeter any; }
    @Component @Primary static class PrimaryGreeter implements Greeter { public String greet() { return "Hi (primary)"; } }
    @Component static class UsesPrimary { @Autowired Greeter greeter; }

    // PURPOSE: Demo - start a context with the ambiguous injection and print the exception chain.
    @Test
    void demo_theStartupFailure() {
        try (var ctx = new AnnotationConfigApplicationContext()) {
            ctx.register(English.class, French.class, Ambiguous.class);
            try {
                ctx.refresh();
            } catch (BeansException e) {
                McqSupport.show("top exception  : %s", e.getClass().getSimpleName());
                McqSupport.show("root cause     : %s", NestedExceptionUtils.getRootCause(e).getClass().getSimpleName());
                McqSupport.show("message        : %s", NestedExceptionUtils.getRootCause(e).getMessage().lines().findFirst().orElse(""));
                assertThat(NestedExceptionUtils.getRootCause(e)).isInstanceOf(NoUniqueBeanDefinitionException.class);
            }
        }
    }

    // PURPOSE: Options A, B, D are WRONG - there is no guessing: the context does not even start (so nothing can be injected, not null either).
    @Test
    void optionsABD_wrong_noSilentGuess() {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(English.class, French.class, Ambiguous.class);
        McqSupport.attempt("context.refresh()", ctx::refresh);
        assertThat(ctx.isActive()).isFalse();
    }

    // PURPOSE: Fix 1 - a field name equal to a bean name resolves the ambiguity (last-resort rule).
    @Test
    void fix_fieldNameMatchesBeanName() {
        try (var ctx = new AnnotationConfigApplicationContext(English.class, French.class, ByFieldName.class)) {
            McqSupport.show("field named 'french' got: %s", ctx.getBean(ByFieldName.class).french.greet());
            assertThat(ctx.getBean(ByFieldName.class).french.greet()).isEqualTo("Bonjour");
        }
    }

    // PURPOSE: Fix 2 - @Qualifier chooses a bean by name explicitly.
    @Test
    void fix_qualifier() {
        try (var ctx = new AnnotationConfigApplicationContext(English.class, French.class, ByQualifier.class)) {
            McqSupport.show("@Qualifier(\"english\") got: %s", ctx.getBean(ByQualifier.class).any.greet());
            assertThat(ctx.getBean(ByQualifier.class).any.greet()).isEqualTo("Hello");
        }
    }

    // PURPOSE: Fix 3 - @Primary marks the default candidate when nothing else says otherwise.
    @Test
    void fix_primary() {
        try (var ctx = new AnnotationConfigApplicationContext(English.class, French.class, PrimaryGreeter.class, UsesPrimary.class)) {
            McqSupport.show("with a @Primary bean, plain @Autowired Greeter got: %s", ctx.getBean(UsesPrimary.class).greeter.greet());
            assertThat(ctx.getBean(UsesPrimary.class).greeter.greet()).isEqualTo("Hi (primary)");
        }
    }
}
