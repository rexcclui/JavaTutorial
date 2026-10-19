package prep.design;

import java.util.function.LongSupplier;

/**
 * Token-bucket rate limiter: allows bursts up to 'capacity' and a steady rate of refillPerSecond afterwards.
 *
 * How it works:
 *   1. The bucket holds tokens, starting full. Each allowed request consumes one token.
 *   2. Tokens are added continuously over time (up to capacity), but they are calculated lazily when a request arrives, so no background thread is needed.
 *   3. Time comes from an injected clock so tests can control it.
 */
public class RateLimiter {
    private final int capacity;
    private final double refillPerNano;
    private final LongSupplier nanoClock;
    private double tokens;
    private long lastRefill;

    /**
     * Creates a full bucket.
     *
     * How it works:
     *   1. Validate the arguments.
     *   2. Convert the refill rate to tokens per nanosecond.
     *   3. Start full and remember the current time as the last refill time.
     *
     * Complexity: O(1)
     */
    public RateLimiter(int capacity, double refillPerSecond, LongSupplier nanoClock) {
        if (capacity <= 0 || refillPerSecond <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        this.refillPerNano = refillPerSecond / 1_000_000_000.0;
        this.nanoClock = nanoClock;
        this.tokens = capacity;
        this.lastRefill = nanoClock.getAsLong();
    }

    /**
     * Takes one token if available. Returns true if the request is allowed, false if it must be rejected.
     *
     * How it works:
     *   1. Work out how much time passed since the last call and add the matching number of tokens, never exceeding capacity.
     *   2. If at least 1 token is available, remove it and return true; otherwise return false.
     *   3. 'synchronized' makes the read-modify-write on tokens and lastRefill atomic across threads.
     *
     * Complexity: O(1)
     */
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
