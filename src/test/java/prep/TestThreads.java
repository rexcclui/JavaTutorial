package prep;

/**
 * Test helper: threads that don't spam stack traces when the code under test throws
 * (e.g. an unsolved exercise throwing UnsupportedOperationException("TODO")).
 * The test still fails through its own assertions / timeouts.
 */
public final class TestThreads {
    private TestThreads() {}

    public static Thread newThread(Runnable body) {
        Thread t = new Thread(body);
        t.setUncaughtExceptionHandler((thread, error) -> { });
        return t;
    }
}
