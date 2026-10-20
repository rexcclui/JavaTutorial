package demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import demo.book.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Starts the whole application context (without a network port) and calls the endpoints with MockMvc. Run: ./serve.sh test */
@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTest {
    @Autowired MockMvc mvc;
    @Autowired BookRepository repository;

    private static final String VALID_BOOK = """
            {"title": "Clean Code", "author": "Robert C. Martin", "price": 33.50}""";

    @Test
    void seededBooksAreListedWithPaging() throws Exception {
        mvc.perform(get("/api/books?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThanOrEqualTo(5)));
    }

    @Test
    void searchByAuthorIgnoresCase() throws Exception {
        mvc.perform(get("/api/books/search?author=george ORWELL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createReturns201WithLocationHeader() throws Exception {
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(VALID_BOOK))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/books/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void invalidBodyReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content("""
                        {"title": "", "author": "X", "price": -1}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("title must not be blank"))
                .andExpect(jsonPath("$.errors.price").value("price must not be negative"));
    }

    @Test
    void unknownIdReturns404ProblemDetail() throws Exception {
        mvc.perform(get("/api/books/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book not found"))
                .andExpect(jsonPath("$.detail").value("No book with id 99999"));
    }

    @Test
    void discountIsPersistedByDirtyCheckingWithoutSave() throws Exception {
        long id = repository.findByAuthorIgnoreCase("Joshua Bloch").get(0).getId();
        mvc.perform(post("/api/books/" + id + "/discount?percent=50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(21.0));
        assertThat(repository.findById(id).orElseThrow().getPrice()).isEqualByComparingTo("21.00");
    }

    @Test
    void discountOutsideAllowedRangeReturns400() throws Exception {
        mvc.perform(post("/api/books/1/discount?percent=95")).andExpect(status().isBadRequest());
    }

    @Test
    void batchWithSimulatedFailureRollsBackEverything() throws Exception {
        long before = repository.count();
        mvc.perform(post("/api/books/batch?failAfterFirst=true").contentType(MediaType.APPLICATION_JSON).content("""
                        [{"title": "A", "author": "A", "price": 1},
                         {"title": "B", "author": "B", "price": 2}]"""))
                .andExpect(status().isConflict());
        assertThat(repository.count()).isEqualTo(before);       // the first book was saved, then rolled back
    }

    @Test
    void batchWithoutFailureStoresAll() throws Exception {
        long before = repository.count();
        mvc.perform(post("/api/books/batch").contentType(MediaType.APPLICATION_JSON).content("""
                        [{"title": "A", "author": "A", "price": 1},
                         {"title": "B", "author": "B", "price": 2}]"""))
                .andExpect(status().isOk());
        assertThat(repository.count()).isEqualTo(before + 2);
    }

    @Test
    void deleteReturns204ThenTheBookIsGone() throws Exception {
        String location = mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(VALID_BOOK))
                .andReturn().getResponse().getHeader("Location");
        String path = location.substring(location.indexOf("/api/"));
        mvc.perform(delete(path)).andExpect(status().isNoContent());
        mvc.perform(get(path)).andExpect(status().isNotFound());
    }

    @Test
    void requestFlowListsFilterInterceptorControllerAndAop() throws Exception {
        mvc.perform(get("/api/debug/flow"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())))
                .andExpect(jsonPath("$[0]", startsWith("1. Servlet Filter")))
                .andExpect(jsonPath("$[1]", startsWith("2. HandlerInterceptor")))
                .andExpect(jsonPath("$[2]", startsWith("3. Controller")))
                .andExpect(jsonPath("$[3]", startsWith("4. AOP")));
    }

    @Test
    void beansInjectedAreProxies() throws Exception {
        mvc.perform(get("/api/debug/proxies"))
                .andExpect(jsonPath("$.bookServiceIsCglibProxy").value(true))
                .andExpect(jsonPath("$.bookRepositoryIsJdkProxy").value(true));
    }

    @Test
    void helloShowsConfigurationAndDefaults() throws Exception {
        mvc.perform(get("/api/hello"))
                .andExpect(jsonPath("$.greeting").value("Hello from the default profile"))
                .andExpect(jsonPath("$.valueWithDefault").value("the default after the colon"))
                .andExpect(jsonPath("$.defaultProfiles", hasItem("default")));
    }

    @Test
    void actuatorHealthIsUp() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(jsonPath("$.status").value("UP"));
    }
}
