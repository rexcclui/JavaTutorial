package demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Typed binding of the "demo.*" keys in application.yml (demo.greeting, demo.max-page-size). Values change per profile. */
@ConfigurationProperties(prefix = "demo")
public record DemoProperties(String greeting, int maxPageSize) {
}
