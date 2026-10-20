package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * QUESTION 46 - @SpringBootApplication
 *
 * @SpringBootApplication is a convenience annotation that combines:
 *
 *   A. @Configuration and @EnableWebMvc
 *   B. @Configuration and @ComponentScan only
 *   C. @EnableAutoConfiguration and @RestController
 *   D. @SpringBootConfiguration (a specialised @Configuration), @EnableAutoConfiguration and @ComponentScan
 *
 * ANSWER: D
 *
 * WHY:
 *   @SpringBootConfiguration - marks the class as a source of bean definitions (it is meta-annotated with @Configuration).
 *   @EnableAutoConfiguration - configures beans automatically from what is on the classpath (data source, MVC, Jackson...).
 *   @ComponentScan           - scans the package of the annotated class and its sub-packages for @Component / @Service / ...
 *   Because of the scan, the main class belongs in the ROOT package of the application.
 *
 * See it run:  ./run.sh mcq Q46
 */
class Q46SpringBootApplicationTest {

    private static boolean has(Class<? extends Annotation> annotation) {
        return SpringBootApplication.class.isAnnotationPresent(annotation);
    }

    // PURPOSE: Demo - ask which annotations @SpringBootApplication itself carries (reflection on the annotation type).
    @Test
    void demo_theComposedAnnotations() {
        List<Class<? extends Annotation>> candidates = List.of(SpringBootConfiguration.class, EnableAutoConfiguration.class, ComponentScan.class,
                Configuration.class, EnableWebMvc.class, RestController.class);
        for (var c : candidates) McqSupport.show("@SpringBootApplication directly carries @%-24s ? %s", c.getSimpleName(), has(c));
        assertThat(has(SpringBootConfiguration.class)).isTrue();
        assertThat(has(EnableAutoConfiguration.class)).isTrue();
        assertThat(has(ComponentScan.class)).isTrue();
    }

    // PURPOSE: Option D is CORRECT - and @SpringBootConfiguration is itself a @Configuration.
    @Test
    void optionD_correct_springBootConfigurationIsAConfiguration() {
        boolean isConfiguration = AnnotatedElementUtils.hasAnnotation(SpringBootConfiguration.class, Configuration.class);
        McqSupport.show("@SpringBootConfiguration meta-annotated with @Configuration? %s", isConfiguration);
        assertThat(isConfiguration).isTrue();
        assertThat(AnnotatedElementUtils.hasAnnotation(SpringBootApplication.class, Configuration.class)).isTrue();   // so @SpringBootApplication counts as a @Configuration too
    }

    // PURPOSE: Option A is WRONG - Spring Boot decides about MVC via auto-configuration; @EnableWebMvc is NOT part of it (adding it would even switch Boot's MVC defaults off).
    @Test
    void optionA_wrong_noEnableWebMvc() {
        assertThat(has(EnableWebMvc.class)).isFalse();
    }

    // PURPOSE: Option B is WRONG - @EnableAutoConfiguration is the part that makes Boot "magic", and it is included.
    @Test
    void optionB_wrong_autoConfigurationIsIncluded() {
        assertThat(has(EnableAutoConfiguration.class)).isTrue();
    }

    // PURPOSE: Option C is WRONG - @RestController marks web controllers; it is not part of the application class annotation.
    @Test
    void optionC_wrong_noRestController() {
        assertThat(has(RestController.class)).isFalse();
    }
}
