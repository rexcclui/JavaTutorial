package prep.design;

import java.util.function.LongSupplier;

/**
 * EXERCISE: Token-bucket rate limiter.
 * - Bucket holds up to `capacity` tokens and starts full.
 * - Refills `refillPerSecond` tokens each second (continuously, fractional tokens allowed internally).
 * - tryAcquire() consumes one token and returns true, or returns false if none are available.
 * - Time comes from the injected `nanoClock` so tests are deterministic. Use it, NOT System.nanoTime().
 * - Must be thread-safe.
 */
public class RateLimiter {
    public RateLimiter(int capacity, double refillPerSecond, LongSupplier nanoClock) {
        // TODO
    }

    public boolean tryAcquire() {
        throw new UnsupportedOperationException("TODO");
    }
}
