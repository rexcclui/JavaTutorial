package demo.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Collects the steps a request goes through, in order, and logs them. GET /api/debug/flow returns the list. */
public final class FlowTrace {
    private static final String ATTRIBUTE = "demo.flowTrace";
    private static final Logger log = LoggerFactory.getLogger("flow");

    private FlowTrace() { }

    @SuppressWarnings("unchecked")
    public static void add(HttpServletRequest request, String step) {
        List<String> steps = (List<String>) request.getAttribute(ATTRIBUTE);
        if (steps == null) {
            steps = new ArrayList<>();
            request.setAttribute(ATTRIBUTE, steps);
        }
        steps.add(step);
        log.info("{}", step);
    }

    @SuppressWarnings("unchecked")
    public static List<String> steps(HttpServletRequest request) {
        List<String> steps = (List<String>) request.getAttribute(ATTRIBUTE);
        return steps == null ? List.of() : new ArrayList<>(steps);
    }
}
