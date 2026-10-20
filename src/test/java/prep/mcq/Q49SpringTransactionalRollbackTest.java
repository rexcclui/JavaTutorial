package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.SimpleTransactionStatus;

/**
 * QUESTION 49 - @Transactional rollback rules
 *
 *     @Transactional public void save() throws IOException { ...; throw new IOException("disk"); }
 *
 * By default, what happens to the transaction when this checked exception leaves the method?
 *
 *   A. It is rolled back.
 *   B. It is rolled back only if the method is private.
 *   C. It is COMMITTED: by default Spring rolls back only for unchecked exceptions (RuntimeException) and Errors.
 *   D. It is suspended.
 *
 * ANSWER: C
 *
 * WHY:
 *   Spring follows the EJB convention: RuntimeException and Error => rollback; checked exceptions => commit (they are treated as "business" outcomes).
 *   To roll back on a checked exception write @Transactional(rollbackFor = Exception.class) (or IOException.class). Use noRollbackFor for the opposite.
 *   The demo uses a recording transaction manager instead of a database, so you can see begin / commit / rollback directly.
 *
 * See it run:  ./run.sh mcq Q49
 */
class Q49SpringTransactionalRollbackTest {
    static final List<String> EVENTS = new ArrayList<>();

    /** Fake transaction manager: records what Spring asks it to do. */
    static class RecordingTxManager implements PlatformTransactionManager {
        @Override public TransactionStatus getTransaction(TransactionDefinition definition) { EVENTS.add("begin"); return new SimpleTransactionStatus(true); }
        @Override public void commit(TransactionStatus status) { EVENTS.add("commit"); }
        @Override public void rollback(TransactionStatus status) { EVENTS.add("rollback"); }
    }

    static class Service {
        @Transactional public void runtimeFailure() { throw new IllegalStateException("runtime"); }
        @Transactional public void checkedFailure() throws IOException { throw new IOException("checked"); }
        @Transactional(rollbackFor = IOException.class) public void checkedFailureWithRollbackFor() throws IOException { throw new IOException("checked"); }
        @Transactional public void success() { }
    }

    @Configuration @EnableTransactionManagement
    static class Cfg {
        @Bean PlatformTransactionManager transactionManager() { return new RecordingTxManager(); }
        @Bean Service service() { return new Service(); }
    }

    private static List<String> run(ThrowingCall call) {
        EVENTS.clear();
        try (var ctx = new AnnotationConfigApplicationContext(Cfg.class)) {
            try {
                call.run(ctx.getBean(Service.class));
            } catch (Exception expected) {
                EVENTS.add("(" + expected.getClass().getSimpleName() + " propagated to the caller)");
            }
        }
        return new ArrayList<>(EVENTS);
    }

    interface ThrowingCall { void run(Service s) throws Exception; }

    // PURPOSE: Demo - four methods, four outcomes: print what the transaction manager was asked to do.
    @Test
    void demo_beginCommitRollback() {
        McqSupport.show("success()                          -> %s", run(Service::success));
        McqSupport.show("runtimeFailure()                   -> %s", run(Service::runtimeFailure));
        McqSupport.show("checkedFailure()  (IOException)    -> %s", run(Service::checkedFailure));
        McqSupport.show("checkedFailureWithRollbackFor()    -> %s", run(Service::checkedFailureWithRollbackFor));
        assertThat(run(Service::success)).containsExactly("begin", "commit");
        assertThat(run(Service::runtimeFailure)).startsWith("begin", "rollback");
    }

    // PURPOSE: Option C is CORRECT - a checked IOException still COMMITS.
    @Test
    void optionC_correct_checkedExceptionCommits() {
        List<String> events = run(Service::checkedFailure);
        assertThat(events).containsExactly("begin", "commit", "(IOException propagated to the caller)");
    }

    // PURPOSE: Option A is WRONG for the default rules - but rollbackFor makes it true.
    @Test
    void optionA_wrongByDefault_rollbackForFixesIt() {
        assertThat(run(Service::checkedFailureWithRollbackFor)).containsExactly("begin", "rollback", "(IOException propagated to the caller)");
    }

    // PURPOSE: Options B and D are WRONG - visibility does not turn rollback on (private/package methods are not even proxied) and 'suspended' is a propagation concept, not an outcome.
    @Test
    void optionsBD_wrong() {
        assertThat(run(Service::checkedFailure)).doesNotContain("rollback");
    }
}
