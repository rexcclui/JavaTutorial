package prep.design;

import java.util.function.LongSupplier;

public class RateLimiter {
    private final int capacity;
    private final double refillPerNano;
    private final LongSupplier nanoClock;
    private double tokens;
    private long lastRefill;

    public RateLimiter(int capacity, double refillPerSecond, LongSupplier nanoClock) {
        if (capacity <= 0 || refillPerSecond <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        this.refillPerNano = refillPerSecond / 1_000_000_000.0;
        this.nanoClock = nanoClock;
        this.tokens = capacity;
        this.lastRefill = nanoClock.getAsLong();
    }

    public synchronized boolean tryAcquire() {
        long now = nanoClock.getAsLong();
        tokens = Math.min(capacity, tokens + (now - lastRefill) * refillPerNano);
        lastRefill = now;
        if (tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
        // TWIST: implement a sliding-window-log limiter instead. Compare memory use and burst behaviour.
    }
}
