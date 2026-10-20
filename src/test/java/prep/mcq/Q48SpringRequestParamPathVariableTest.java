package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * QUESTION 48 - @PathVariable vs @RequestParam
 *
 *     GET /users/42?active=true
 *     @GetMapping("/users/{id}") String user(@PathVariable("id") int id, @RequestParam("active") boolean active)
 *
 * Which statement is correct?
 *
 *   A. @PathVariable reads query-string parameters; @RequestParam reads segments of the URL path.
 *   B. @PathVariable binds a segment of the URL path ({id} -> 42); @RequestParam binds a query-string (or form) parameter (?active=true).
 *   C. They are interchangeable.
 *   D. @RequestParam can only read the request body of a POST.
 *
 * ANSWER: B
 *
 * WHY:
 *   Path variables identify a RESOURCE (/users/42). Request parameters FILTER, SORT or PAGE it (?active=true&page=2).
 *   A required parameter that is missing gives HTTP 400 Bad Request; use required = false or defaultValue to make it optional.
 *   (The request body of a POST/PUT is read with @RequestBody.)
 *
 * See it run:  ./run.sh mcq Q48     (HTTP calls are simulated with MockMvc: no server is started)
 */
class Q48SpringRequestParamPathVariableTest {
    @RestController
    static class UserController {
        @GetMapping("/users/{id}")
        String user(@PathVariable("id") int id, @RequestParam("active") boolean active) {
            return "id=" + id + ", active=" + active;
        }

        @GetMapping("/search")
        String search(@RequestParam(name = "q", defaultValue = "all") String q, @RequestParam(name = "page", required = false) Integer page) {
            return "q=" + q + ", page=" + page;
        }
    }

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController()).build();

    private MvcResult call(String url) throws Exception {
        return mvc.perform(get(url)).andReturn();
    }

    // PURPOSE: Demo - GET /users/42?active=true: which part of the URL went into which parameter?
    @Test
    void demo_urlParts() throws Exception {
        MvcResult r = call("/users/42?active=true");
        McqSupport.show("GET /users/42?active=true  ->  %d  \"%s\"", r.getResponse().getStatus(), r.getResponse().getContentAsString());
        assertThat(r.getResponse().getContentAsString()).isEqualTo("id=42, active=true");
    }

    // PURPOSE: Option B is CORRECT - the path segment fills @PathVariable; swapping the roles in the URL breaks it (400/404), proving they read different parts.
    @Test
    void optionB_correct_differentPartsOfTheUrl() throws Exception {
        MvcResult swapped = call("/users/active?id=42");
        McqSupport.show("GET /users/active?id=42    ->  %d  (path segment 'active' is not an int, and ?active is missing)", swapped.getResponse().getStatus());
        assertThat(swapped.getResponse().getStatus()).isEqualTo(400);
    }

    // PURPOSE: Options A and C are WRONG - a missing REQUEST parameter gives 400 even though the path variable is present; path and parameters are not interchangeable.
    @Test
    void optionsAC_wrong_missingRequestParamIsA400() throws Exception {
        MvcResult r = call("/users/42");
        McqSupport.show("GET /users/42 (no ?active)  ->  %d", r.getResponse().getStatus());
        assertThat(r.getResponse().getStatus()).isEqualTo(400);
    }

    // PURPOSE: Option D is WRONG - @RequestParam reads query parameters of a GET request (here ?q=spring).
    @Test
    void optionD_wrong_requestParamReadsTheQueryString() throws Exception {
        MvcResult r = call("/search?q=spring&page=2");
        McqSupport.show("GET /search?q=spring&page=2  ->  \"%s\"", r.getResponse().getContentAsString());
        assertThat(r.getResponse().getContentAsString()).isEqualTo("q=spring, page=2");
    }

    // PURPOSE: Making parameters optional - defaultValue fills in a missing value, required = false allows null.
    @Test
    void optional_defaultValueAndRequiredFalse() throws Exception {
        MvcResult r = call("/search");
        McqSupport.show("GET /search (no parameters)  ->  \"%s\"", r.getResponse().getContentAsString());
        assertThat(r.getResponse().getContentAsString()).isEqualTo("q=all, page=null");
    }
}
