package prep.mcq.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Top-level sample configuration for Q44 (bean names): one @Bean with the default name, one with an explicit name. */
@Configuration
public class SampleConfig {
    @Bean
    public String makeInvoice() {
        return "invoice";
    }

    @Bean("custom")
    public String second() {
        return "second";
    }
}
