# A real Spring Boot server to play with (`spring-demo/`)

A small book-store REST API, built so you can WATCH Spring work: start it, call it with `curl`, read the console, change the code, call it again.
It is a separate Maven project (`spring-demo/`), so it does not affect the interview-practice build.

## Start it

```bash
./serve.sh                 # http://localhost:8080   (Ctrl+C stops it)
./serve.sh prod            # same server with the "prod" profile
./serve.sh --port 9090     # another port
./serve.sh test            # the demo's 14 automated tests
```

The first start downloads Spring Boot (1-2 minutes). Later starts take about 6 seconds. You need JDK 21 and Maven (same as the rest of the repo).
The database is H2 **in memory**: it is recreated (and re-seeded with 5 books) on every start.

## Try it (all calls are also in `spring-demo/requests.http` for IntelliJ / VS Code)

```bash
curl "localhost:8080/api/books?page=0&size=2"                 # paged list
curl localhost:8080/api/books/1                               # one book
curl localhost:8080/api/books/999                             # 404 problem-detail JSON
curl "localhost:8080/api/books/search?author=george%20orwell" # derived query
curl -i -X POST localhost:8080/api/books -H 'Content-Type: application/json' \
     -d '{"title":"Clean Code","author":"Robert C. Martin","price":33.50}'      # 201 + Location header
curl -X POST localhost:8080/api/books -H 'Content-Type: application/json' \
     -d '{"title":"","author":"X","price":-1}'                # 400 with the bad fields listed
curl -X POST "localhost:8080/api/books/5/discount?percent=50" # UPDATE issued by dirty checking
curl localhost:8080/api/hello                                 # configuration + active profile
curl localhost:8080/api/debug/flow                            # the journey of a request
curl localhost:8080/api/debug/proxies                         # injected beans are generated proxies
curl localhost:8080/actuator/beans                            # every bean in the container
curl localhost:8080/actuator/mappings                         # every URL mapping
```

Also open **http://localhost:8080/h2-console** (JDBC URL `jdbc:h2:mem:demo`, user `sa`, empty password) and run `select * from book`.

## What to watch in the console

Every request prints its journey on the `flow` logger, in this order:

```
1. Servlet Filter (before): GET /api/debug/flow  [request id 87afb99a]
2. HandlerInterceptor.preHandle: DispatcherServlet chose demo.InfoController#flow[1 args]
3. Controller method InfoController.flow() is running
4. AOP @Around (TimingAspect) -> BookService.count()
5. HandlerInterceptor.postHandle: controller returned normally (response not yet committed)
6. Servlet Filter (after): response status 200 in 4 ms  [request id 87afb99a]
```

and every SQL statement Hibernate sends is printed (`select`, `insert`, `update`...).

## Experiments (each one teaches an interview topic)

| Do this | Watch | Topic (see also the MCQ question) |
|---|---|---|
| `POST .../5/discount?percent=50` | an `update book set ...` appears although `BookService.applyDiscount` never calls `save()` | JPA dirty checking, `@Transactional` |
| `POST .../batch?failAfterFirst=true`, then `GET /api/books` | the first book was inserted, then rolled back: the count did not change | Transaction rollback on `RuntimeException` (Q49) |
| In `BookService.createAll` throw `new java.io.IOException("x")` instead of the IllegalStateException (add `throws Exception` to `createAll` and to `BookController.batch`), then call the failing batch again | the HTTP call fails with a 500 but the first book is now COMMITTED: the count went up | Checked exceptions do not roll back by default (Q49); fix with `@Transactional(rollbackFor = Exception.class)` |
| `GET /api/debug/proxies` | `BookService$$SpringCGLIB$$0` and `jdk.proxy2.$Proxy...` | Proxies behind `@Transactional` / Spring Data (Q41, Q39) |
| Add `public Book firstBook() { return this.get(1L); }` to `BookService`, add an endpoint in `InfoController` that calls `bookService.firstBook()`, and read the console | the AOP line appears for `firstBook()` only; the inner `this.get(1L)` call is not advised or timed | Self-invocation trap (Q41) |
| `./serve.sh prod`, then `GET /api/hello` and `GET /api/books` | other greeting, page size 5, EMPTY list (the `DataSeeder` is `@Profile("!prod")`) | Profiles, `@ConfigurationProperties` (Q45) |
| `GET /api/books?size=500` | the result is capped by `demo.max-page-size` from `application.yml` | Externalised configuration |
| `POST` with an empty title | 400 JSON built by `GlobalExceptionHandler` | `@Valid`, `@RestControllerAdvice` |
| `DELETE /api/books` or `POST` with `Content-Type: text/plain` | 405 / 415 problem details, no code written by us | Spring MVC default exception handling |
| `GET /actuator/beans` and search for `bookService` | scope, dependencies, the class of every bean | Bean scopes and wiring (Q36, Q44) |
| Remove `@Service` from `BookService` and start | startup FAILS: `BookController` requires a `BookService` bean | Component scanning, fail-fast injection (Q37, Q43) |
| Add `@Component("bookService")` to a second class | startup fails: duplicate / ambiguous bean | Bean naming (Q44) |
| Make `BookController` depend on a class that depends back on it (constructor injection) | startup fails with a circular reference | Circular dependencies (Q42) |

## Project map

```
spring-demo/src/main/java/demo/
  DemoApplication.java            @SpringBootApplication: configuration + auto-configuration + component scan
  book/Book.java                  @Entity (table row) + validation annotations
  book/BookRepository.java        Spring Data interface; Spring generates the implementation
  book/BookService.java           business layer; @Transactional
  book/BookController.java        @RestController: URL -> method, JSON in and out
  web/GlobalExceptionHandler.java @RestControllerAdvice: exceptions -> HTTP responses
  web/RequestLoggingFilter.java   servlet Filter (outermost layer)
  web/FlowInterceptor.java        Spring MVC HandlerInterceptor
  aop/TimingAspect.java           @Aspect around BookService
  config/DemoProperties.java      @ConfigurationProperties("demo")
  DataSeeder.java                 CommandLineRunner, @Profile("!prod")
  InfoController.java             /api/hello and /api/debug/* teaching endpoints
spring-demo/src/main/resources/application.yml        main configuration (with comments)
spring-demo/src/main/resources/application-prod.yml   overrides for the prod profile
```

## Layers of one request

```
HTTP request
  -> Servlet Filter                 (raw request/response: logging, security, CORS)
  -> DispatcherServlet              (picks the controller method from the URL + HTTP verb)
  -> HandlerInterceptor.preHandle   (knows the chosen method)
  -> argument binding               (@PathVariable, @RequestParam, @RequestBody + @Valid)
  -> Controller                     (web layer: no business logic)
  -> Service proxy                  (AOP advice, then the TRANSACTION begins)
  -> Service method                 (business logic)
  -> Repository proxy -> Hibernate -> SQL -> H2
  <- commit / rollback, result back up
  <- Jackson writes the JSON      (or @ExceptionHandler turns an exception into an error response)
  <- HandlerInterceptor.postHandle / afterCompletion
  <- Servlet Filter (after)
HTTP response
```

## Safety note

`application.yml` exposes `beans`, `env` and `configprops` through Actuator. That is perfect for learning and wrong for production, where you expose only `health` (and protect the rest).
