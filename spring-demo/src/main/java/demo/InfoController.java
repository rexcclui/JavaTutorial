package demo;

import demo.book.BookRepository;
import demo.book.BookService;
import demo.config.DemoProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import demo.web.FlowTrace;

/** Endpoints that exist only to show how Spring works. */
@RestController
@RequestMapping("/api")
public class InfoController {
    private final DemoProperties properties;
    private final Environment environment;
    private final ApplicationContext context;
    private final BookService bookService;
    private final BookRepository bookRepository;

    @Value("${demo.not-defined:the default after the colon}")       // a missing property falls back to the default
    private String valueWithDefault;

    public InfoController(DemoProperties properties, Environment environment, ApplicationContext context, BookService bookService, BookRepository bookRepository) {
        this.properties = properties;
        this.environment = environment;
        this.context = context;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    /** GET /api/hello - configuration: values from application.yml, the active profile, and a @Value default. */
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("greeting", properties.greeting());
        result.put("maxPageSize", properties.maxPageSize());
        result.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        result.put("defaultProfiles", Arrays.asList(environment.getDefaultProfiles()));
        result.put("valueWithDefault", valueWithDefault);
        return result;
    }

    /**
     * GET /api/debug/flow - the journey of THIS request through the layers, in order. The same lines appear in the console log.
     * (Steps 5 and 6 happen after this method returned, so they only appear in the console.)
     */
    @GetMapping("/debug/flow")
    public List<String> flow(HttpServletRequest request) {
        FlowTrace.add(request, "3. Controller method InfoController.flow() is running");
        bookService.count();                                // goes through the AOP + transaction proxy -> step 4
        List<String> steps = FlowTrace.steps(request);
        steps.add("(5. Interceptor.postHandle and 6. Filter-after run after this method returns: see the console)");
        return steps;
    }

    /**
     * GET /api/debug/stack - the REAL chain of method calls, from the web server's thread down to THIS method (outermost first).
     * Only the interesting frames are kept: Tomcat, filters, DispatcherServlet, the handler adapter and your own code.
     */
    @GetMapping("/debug/stack")
    public List<String> stack() {
        List<String> frames = new ArrayList<>();
        frames.add("thread: " + Thread.currentThread().getName());
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = trace.length - 1; i >= 0; i--) {              // the array is innermost-first; walk it outermost-first
            String cls = trace[i].getClassName();
            boolean interesting = cls.equals("org.apache.tomcat.util.net.NioEndpoint$SocketProcessor")
                    || cls.equals("org.apache.coyote.http11.Http11Processor")
                    || cls.equals("org.apache.catalina.core.StandardWrapperValve")
                    || trace[i].getMethodName().equals("doFilterInternal")      // every Spring servlet filter, one line each
                    || cls.equals("org.springframework.web.servlet.DispatcherServlet")
                    || cls.equals("org.springframework.web.servlet.FrameworkServlet")
                    || cls.equals("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter")
                    || cls.equals("org.springframework.web.method.support.InvocableHandlerMethod")
                    || cls.startsWith("demo.");
            if (!interesting) continue;
            String simple = cls.substring(cls.lastIndexOf('.') + 1) + "." + trace[i].getMethodName() + "()";
            if (!frames.isEmpty() && frames.get(frames.size() - 1).equals(simple)) continue;   // collapse repeats
            frames.add(simple);
        }
        return frames;
    }

    /** GET /api/debug/proxies - the objects Spring injected are not your classes, they are generated proxies. */
    @GetMapping("/debug/proxies")
    public Map<String, Object> proxies() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("bookServiceClass", bookService.getClass().getName());
        result.put("bookServiceIsAopProxy", AopUtils.isAopProxy(bookService));
        result.put("bookServiceIsCglibProxy", AopUtils.isCglibProxy(bookService));
        result.put("bookRepositoryClass", bookRepository.getClass().getName());
        result.put("bookRepositoryIsJdkProxy", AopUtils.isJdkDynamicProxy(bookRepository));
        result.put("explanation", "BookService is wrapped by a CGLIB subclass proxy (transactions + the timing aspect); BookRepository is a JDK dynamic proxy generated by Spring Data from the interface.");
        return result;
    }

    /** GET /api/debug/beans - how big the container is. Full list: /actuator/beans. */
    @GetMapping("/debug/beans")
    public Map<String, Object> beans() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("beanDefinitionCount", context.getBeanDefinitionCount());
        result.put("yourBeans", Arrays.stream(context.getBeanDefinitionNames()).filter(name -> context.getBean(name).getClass().getName().startsWith("demo.")).sorted().toList());
        result.put("hint", "All beans, their dependencies and scopes: GET /actuator/beans");
        return result;
    }
}
