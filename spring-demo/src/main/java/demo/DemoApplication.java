package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point. @SpringBootApplication =
 *   @SpringBootConfiguration (this class is a configuration) + @EnableAutoConfiguration (configure web server, JPA, Jackson... from the classpath)
 *   + @ComponentScan (find @Component / @Service / @RestController... in this package and below).
 * That is why this class sits in the ROOT package "demo".
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
