package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * QUESTION 47 - @Controller vs @RestController
 *
 *     @Controller      class A { @GetMapping("/greet") String greet() { return "hello"; } }
 *     @RestController  class B { @GetMapping("/greet") String greet() { return "hello"; } }
 *
 * What is the difference for a request to /greet?
 *
 *   A. In A the returned String is a VIEW NAME (the response is a forward to the view "hello"); in B, @RestController = @Controller + @ResponseBody, so "hello" is written as the response body.
 *   B. No difference.
 *   C. @RestController can only return XML.
 *   D. @Controller methods cannot return a String.
 *
 * ANSWER: A
 *
 * WHY:
 *   In classic Spring MVC a controller returns a view name that a ViewResolver turns into a page (Thymeleaf, JSP...).
 *   @ResponseBody (or @RestController, which includes it) says "this return value IS the response body", converted by an HttpMessageConverter (String as text, objects as JSON).
 *   Use @Controller for HTML pages, @RestController for REST APIs.
 *
 * See it run:  ./run.sh mcq Q47     (the HTTP calls are simulated with MockMvc: no server is started)
 */
class Q47SpringControllerVsRestControllerTest {
    @Controller static class ViewController { @GetMapping("/greet") String greet() { return "hello"; } }
    @RestController static class BodyController { @GetMapping("/greet") String greet() { return "hello"; } }
    @Controller static class MixedController { @GetMapping("/greet") @ResponseBody String greet() { return "hello"; } }

    private static MvcResult call(Object controller) throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
        return mvc.perform(get("/greet")).andReturn();
    }

    // PURPOSE: Demo - the same request against both controllers; print the response body and the view the request was forwarded to.
    @Test
    void demo_sameMethodTwoBehaviours() throws Exception {
        MvcResult asView = call(new ViewController());
        MvcResult asBody = call(new BodyController());
        McqSupport.show("@Controller     : status %d, body = \"%s\", forwarded to view \"%s\"", asView.getResponse().getStatus(), asView.getResponse().getContentAsString(), asView.getResponse().getForwardedUrl());
        McqSupport.show("@RestController : status %d, body = \"%s\", forwarded to view %s", asBody.getResponse().getStatus(), asBody.getResponse().getContentAsString(), asBody.getResponse().getForwardedUrl());
        assertThat(asView.getResponse().getForwardedUrl()).isEqualTo("hello");
        assertThat(asView.getResponse().getContentAsString()).isEmpty();
        assertThat(asBody.getResponse().getContentAsString()).isEqualTo("hello");
        assertThat(asBody.getResponse().getForwardedUrl()).isNull();
    }

    // PURPOSE: Option A is CORRECT - and @Controller + @ResponseBody on the method behaves like @RestController.
    @Test
    void optionA_correct_responseBodyMakesTheDifference() throws Exception {
        MvcResult mixed = call(new MixedController());
        McqSupport.show("@Controller + @ResponseBody on the method: body = \"%s\"", mixed.getResponse().getContentAsString());
        assertThat(mixed.getResponse().getContentAsString()).isEqualTo("hello");
    }

    // PURPOSE: Option B is WRONG - the two produce different responses (see demo).
    @Test
    void optionB_wrong_behaviourDiffers() throws Exception {
        assertThat(call(new ViewController()).getResponse().getContentAsString()).isNotEqualTo(call(new BodyController()).getResponse().getContentAsString());
    }

    // PURPOSE: Options C and D are WRONG - @RestController returned plain text here, and @Controller returned a String (a view name) without any problem.
    @Test
    void optionsCD_wrong() throws Exception {
        assertThat(call(new BodyController()).getResponse().getContentType()).startsWith("text/plain");
        assertThat(call(new ViewController()).getResponse().getStatus()).isEqualTo(200);
    }
}
