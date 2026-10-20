package demo.config;

import demo.web.FlowInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registers the HandlerInterceptor (it is not a bean that Boot picks up by itself, unlike a servlet Filter). */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FlowInterceptor()).addPathPatterns("/api/**");
    }
}
