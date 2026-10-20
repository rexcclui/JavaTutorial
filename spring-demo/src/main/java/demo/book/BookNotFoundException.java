package demo.book;

/** Thrown by the service when an id does not exist; GlobalExceptionHandler turns it into HTTP 404. */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("No book with id " + id);
    }
}
