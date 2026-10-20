package demo.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A SERVLET FILTER: the outermost layer. It sees the raw request before Spring MVC does and the response after Spring MVC is finished.
 * Typical uses: logging, authentication tokens, CORS, compression. Declared as a @Component, so Spring Boot registers it automatically.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger("flow");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        response.setHeader("X-Request-Id", requestId);
        long start = System.nanoTime();
        FlowTrace.add(request, "1. Servlet Filter (before): " + request.getMethod() + " " + request.getRequestURI() + "  [request id " + requestId + "]");
        try {
            chain.doFilter(request, response);              // -> DispatcherServlet -> interceptors -> controller
        } finally {
            log.info("6. Servlet Filter (after): response status {} in {} ms  [request id {}]", response.getStatus(), (System.nanoTime() - start) / 1_000_000, requestId);
        }
    }
}
