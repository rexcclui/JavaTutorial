package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * QUESTION 40 - prototype bean injected into a singleton
 *
 *     @Component @Scope("prototype") class Task { }
 *     @Component class Worker { @Autowired Task task; }          // Worker is a singleton
 *
 * How many different Task objects does worker.task refer to across 100 calls?
 *
 *   A. A new one on every call.
 *   B. Always the same one: it is injected once, when the singleton is created.
 *   C. An exception is thrown because the scopes do not match.
 *   D. One per thread.
 *
 * ANSWER: B
 *
 * WHY:
 *   Injection happens once, when the singleton is built. "Prototype" only means a new instance per REQUEST TO THE CONTAINER, and the singleton asks just once.
 *   To get a fresh one each time, ask the container again: inject ObjectProvider<Task> (or ApplicationContext, @Lookup, or a scoped proxy) and call getObject().
 *
 * See it run:  ./run.sh mcq Q40
 */
class Q40SpringPrototypeInSingletonTest {
    @Component @Scope("prototype") static class Task { }

    @Component static class Worker {
        @Autowired Task task;
        @Autowired ObjectProvider<Task> taskProvider;
    }

    // PURPOSE: Demo - read the injected field several times, and ask the provider several times; print identities.
    @Test
    void demo_injectedFieldVersusProvider() {
        try (var ctx = new AnnotationConfigApplicationContext(Task.class, Worker.class)) {
            Worker worker = ctx.getBean(Worker.class);
            for (int i = 1; i <= 3; i++) McqSupport.show("call %d: worker.task identity = %d   (always the same)", i, System.identityHashCode(worker.task));
            for (int i = 1; i <= 3; i++) McqSupport.show("call %d: taskProvider.getObject() identity = %d   (a new Task each time)", i, System.identityHashCode(worker.taskProvider.getObject()));
            assertThat(worker.task).isSameAs(worker.task);
            assertThat(worker.taskProvider.getObject()).isNotSameAs(worker.taskProvider.getObject());
        }
    }

    // PURPOSE: Option A is WRONG - the field is not re-injected; asking the container directly is what gives a new object.
    @Test
    void optionA_wrong_fieldIsNotRefreshed() {
        try (var ctx = new AnnotationConfigApplicationContext(Task.class, Worker.class)) {
            Worker worker = ctx.getBean(Worker.class);
            Task first = worker.task;
            assertThat(ctx.getBean(Task.class)).isNotSameAs(first);        // the container does hand out new prototypes...
            assertThat(worker.task).isSameAs(first);                       // ...but the singleton keeps its original one
        }
    }

    // PURPOSE: Option B is CORRECT - 100 reads, exactly one distinct object.
    @Test
    void optionB_correct_oneInstanceForever() {
        try (var ctx = new AnnotationConfigApplicationContext(Task.class, Worker.class)) {
            Worker worker = ctx.getBean(Worker.class);
            long distinct = java.util.stream.IntStream.range(0, 100).mapToObj(i -> worker.task).distinct().count();
            McqSupport.show("100 reads of worker.task -> %d distinct object(s)", distinct);
            assertThat(distinct).isEqualTo(1);
        }
    }

    // PURPOSE: Option C is WRONG - mixing scopes is allowed; the container starts normally.
    @Test
    void optionC_wrong_noException() {
        McqSupport.attempt("start a context with singleton + prototype", () -> new AnnotationConfigApplicationContext(Task.class, Worker.class).close());
    }
}
