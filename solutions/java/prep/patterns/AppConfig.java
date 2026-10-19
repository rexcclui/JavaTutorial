package prep.patterns;

/** Initialization-on-demand holder: lazy, thread-safe, no explicit synchronization (class loading guarantees it). */
public final class AppConfig {
    private AppConfig() {}

    private static final class Holder {
        static final AppConfig INSTANCE = new AppConfig();
    }

    public static AppConfig getInstance() {
        return Holder.INSTANCE;
    }
    // TWIST: break it with reflection (setAccessible on the constructor). Why is an enum singleton immune?
    // TWIST: break it with serialization. What does readResolve() do?
}
