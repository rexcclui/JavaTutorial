package demo.book;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The business layer. @Transactional on the class means: every public method runs inside a database transaction
 * (readOnly = true by default here; methods that change data override it). Spring does this with a PROXY around this bean:
 * it begins the transaction, calls the method, then commits - or rolls back if a RuntimeException escapes.
 */
@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository repository;                // constructor injection: no @Autowired needed

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Page<Book> page(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public List<Book> search(String author, String title, BigDecimal maxPrice) {
        if (author != null) return repository.findByAuthorIgnoreCase(author);
        if (title != null) return repository.searchTitle(title);
        if (maxPrice != null) return repository.findByPriceLessThanEqualOrderByPrice(maxPrice);
        return repository.findAll();
    }

    public Book get(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public long count() {
        return repository.count();
    }

    @Transactional
    public Book create(Book book) {
        book.setId(null);                                   // never trust an id sent by the client
        return repository.save(book);
    }

    @Transactional
    public Book update(Long id, Book changes) {
        Book book = get(id);
        book.setTitle(changes.getTitle());
        book.setAuthor(changes.getAuthor());
        book.setPrice(changes.getPrice());
        return book;                                        // no save() call: see applyDiscount
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(get(id));
    }

    /**
     * DIRTY CHECKING: there is no repository.save() here. The entity returned by get() is "managed" by Hibernate inside the transaction;
     * when the transaction commits, Hibernate notices the changed price and issues the UPDATE by itself (watch the SQL in the console).
     */
    @Transactional
    public Book applyDiscount(Long id, int percent) {
        Book book = get(id);
        BigDecimal factor = BigDecimal.valueOf(100 - percent).divide(BigDecimal.valueOf(100));
        book.setPrice(book.getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        return book;
    }

    /**
     * ROLLBACK DEMO: saves every book; if failAfterFirst is true it throws a RuntimeException after the first save.
     * The exception passes through the @Transactional proxy, which rolls the whole transaction back: NOTHING is stored, not even the first book.
     */
    @Transactional
    public List<Book> createAll(List<Book> books, boolean failAfterFirst) {
        List<Book> saved = new ArrayList<>();
        for (Book book : books) {
            book.setId(null);
            saved.add(repository.save(book));
            if (failAfterFirst && saved.size() == 1) {
                throw new IllegalStateException("Simulated failure after saving the first book");
            }
        }
        return saved;
    }
}
