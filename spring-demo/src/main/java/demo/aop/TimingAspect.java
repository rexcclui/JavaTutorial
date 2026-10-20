package demo.aop;

import demo.web.FlowTrace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ASPECT-ORIENTED PROGRAMMING: extra behaviour wrapped around existing methods without touching them.
 * This advice wraps every method of BookService (the same proxy mechanism that gives you @Transactional) and logs how long it took.
 * Note: calls from one BookService method to another BookService method bypass the proxy and are NOT timed (self-invocation).
 */
@Aspect
@Component
public class TimingAspect {
    private static final Logger log = LoggerFactory.getLogger("flow");

    @Around("execution(* demo.book.BookService.*(..))")
    public Object time(ProceedingJoinPoint joinPoint) throws Throwable {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            FlowTrace.add(attributes.getRequest(), "4. AOP @Around (TimingAspect) -> " + joinPoint.getSignature().toShortString());
        }
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();                     // run the real method (inside the transaction proxy)
        } finally {
            log.debug("   {} took {} ms", joinPoint.getSignature().toShortString(), (System.nanoTime() - start) / 1_000_000);
        }
    }
}
