package demo.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * A HANDLER INTERCEPTOR: part of Spring MVC, runs after the DispatcherServlet has chosen the controller method but before it is called.
 * Unlike a servlet Filter it knows WHICH controller method will handle the request.
 */
public class FlowInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger("flow");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String target = handler instanceof HandlerMethod hm ? hm.getShortLogMessage() : String.valueOf(handler);
        FlowTrace.add(request, "2. HandlerInterceptor.preHandle: DispatcherServlet chose " + target);
        return true;                                        // false would stop the request here
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("5. HandlerInterceptor.postHandle: controller returned normally (response not yet committed)");
    }
}
