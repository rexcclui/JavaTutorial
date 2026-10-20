package demo.book;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * You write ONLY an interface. At startup Spring Data creates a proxy class that implements it (look at /api/debug/proxies).
 * Method names are parsed into SQL ("derived queries"); @Query lets you write the query yourself.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /** select ... from book where lower(author) = lower(?) */
    List<Book> findByAuthorIgnoreCase(String author);

    /** select ... from book where price <= ? order by price */
    List<Book> findByPriceLessThanEqualOrderByPrice(BigDecimal maxPrice);

    /** Hand-written JPQL query with a named parameter. */
    @Query("select b from Book b where lower(b.title) like lower(concat('%', :text, '%'))")
    List<Book> searchTitle(@Param("text") String text);

    /** Paging comes for free by adding a Pageable parameter. */
    Page<Book> findAll(Pageable pageable);
}
