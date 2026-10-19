package prep.patterns;

/**
 * EXERCISE: Singleton. Make getInstance() return the SAME instance from every thread,
 * with the instance created lazily and the constructor private.
 * Try: (1) holder idiom, (2) double-checked locking with volatile, (3) enum singleton.
 * Question to answer: why is a naive `if (instance == null) instance = new AppConfig();` broken?
 */
public class AppConfig {
    public AppConfig() {                // TODO: should not be public
    }

    public static AppConfig getInstance() {
        return new AppConfig();         // TODO: broken on purpose
    }
}
