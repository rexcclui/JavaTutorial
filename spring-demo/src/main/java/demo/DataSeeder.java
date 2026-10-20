package demo;

import demo.book.Book;
import demo.book.BookRepository;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Runs once at startup, after the application context is ready (CommandLineRunner).
 * @Profile("!prod") means: this bean exists in every profile EXCEPT prod. Start with --spring.profiles.active=prod and /api/books is empty.
 */
@Component
@Profile("!prod")
public class DataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final BookRepository repository;

    public DataSeeder(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        repository.saveAll(List.of(
                new Book("1984", "George Orwell", new BigDecimal("9.99")),
                new Book("Animal Farm", "George Orwell", new BigDecimal("7.50")),
                new Book("The Hobbit", "J.R.R. Tolkien", new BigDecimal("12.00")),
                new Book("The Lord of the Rings", "J.R.R. Tolkien", new BigDecimal("25.00")),
                new Book("Effective Java", "Joshua Bloch", new BigDecimal("42.00"))));
        log.info("Seeded {} books (profile != prod)", repository.count());
    }
}
