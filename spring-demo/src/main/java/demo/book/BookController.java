package demo.book;

import demo.config.DemoProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * The web layer: maps HTTP requests to methods and HTTP responses from return values.
 * @RestController = @Controller + @ResponseBody: return values are written as JSON (by Jackson), not treated as view names.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService service;
    private final DemoProperties properties;

    public BookController(BookService service, DemoProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    /** GET /api/books?page=0&size=3 - @RequestParam reads the query string; defaults make them optional. */
    @GetMapping
    public PageView<Book> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        int cappedSize = Math.min(size, properties.maxPageSize());          // demo.max-page-size comes from application.yml (differs per profile)
        return PageView.of(service.page(page, cappedSize));
    }

    /** GET /api/books/search?author=orwell  (or ?title=ring, or ?maxPrice=10) */
    @GetMapping("/search")
    public List<Book> search(@RequestParam(required = false) String author,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) BigDecimal maxPrice) {
        return service.search(author, title, maxPrice);
    }

    /** GET /api/books/3 - @PathVariable reads a segment of the URL path. Unknown id -> BookNotFoundException -> 404. */
    @GetMapping("/{id}")
    public Book get(@PathVariable Long id) {
        return service.get(id);
    }

    /** POST /api/books with a JSON body. @Valid runs the Bean Validation annotations of Book; failure -> 400 with the list of bad fields. */
    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody Book book) {
        Book saved = service.create(book);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);                // 201 Created + Location header
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody Book book) {
        return service.update(id, book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();                          // 204 No Content
    }

    /** POST /api/books/3/discount?percent=10 - changes the price with dirty checking (see BookService.applyDiscount). */
    @PostMapping("/{id}/discount")
    public Book discount(@PathVariable Long id, @RequestParam @Min(1) @Max(90) int percent) {
        return service.applyDiscount(id, percent);
    }

    /** POST /api/books/batch?failAfterFirst=true with a JSON array of books: demonstrates transaction ROLLBACK. */
    @PostMapping("/batch")
    public List<Book> batch(@Valid @RequestBody List<Book> books, @RequestParam(defaultValue = "false") boolean failAfterFirst) {
        return service.createAll(books, failAfterFirst);
    }
}
