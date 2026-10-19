# Design Patterns

Exercises: `AppConfig` (Singleton), `Pizza` (Builder), `EventBus` (Observer) · Tests: matching `*Test` classes
Run: `./run.sh patterns`

## The ones interviewers actually ask
- **Singleton**: one instance. Best options: enum singleton, initialization-on-demand holder, or DI container scope. Naive lazy init is a race; double-checked locking needs `volatile`. Beware reflection and serialization breaking it.
- **Factory (Method / Abstract)**: hide `new` behind a method/interface so callers depend on abstractions.
- **Builder**: many optional params, immutability, validation in `build()`. Alternative to telescoping constructors.
- **Strategy**: swap an algorithm at runtime (in Java: often just a lambda/`Comparator`).
- **Observer**: one-to-many notification (`EventBus`, listeners). Watch memory leaks (forgotten unsubscribe) and exceptions in listeners.
- **Decorator**: wrap to add behaviour (`BufferedInputStream(new FileInputStream(...))`).
- **Adapter, Proxy, Template Method, Command, State** are also fair game.

## SOLID in one line each
- **S**ingle responsibility · **O**pen/closed · **L**iskov substitution · **I**nterface segregation · **D**ependency inversion.

## Common interview questions
1. Implement a thread-safe Singleton. Why is `volatile` needed in DCL?
2. Factory vs Abstract Factory vs Builder?
3. Where does the JDK use Observer, Decorator, Factory, Iterator, Strategy? (`java.io`, `Comparator`, `Collections`, `Calendar.getInstance`)
4. Inheritance vs composition: why "favour composition"?

## Links
- Refactoring Guru (Java examples): https://refactoring.guru/design-patterns/java
- Book: *Head First Design Patterns*; *Effective Java* items 1-3 (static factories, builders, singletons)
