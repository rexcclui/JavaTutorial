package prep.patterns;

/**
 * Singleton: only one AppConfig can ever exist (initialization-on-demand holder idiom).
 *
 * How it works:
 *   1. The constructor is private, so nobody else can create instances.
 *   2. The static inner class Holder is not loaded until getInstance() first touches it; the JVM guarantees class loading happens once and is thread-safe.
 *   3. So the instance is created lazily and safely without any synchronized or volatile code.
 */
public final class AppConfig {
    private AppConfig() {}

    private static final class Holder {
        static final AppConfig INSTANCE = new AppConfig();
    }

    /**
     * Returns the one shared instance, creating it on first use.
     *
     * Complexity: O(1)
     */
    public static AppConfig getInstance() {
        return Holder.INSTANCE;
    }
    // TWIST: break it with reflection (setAccessible on the constructor). Why is an enum singleton immune?
    // TWIST: break it with serialization. What does readResolve() do?
}
