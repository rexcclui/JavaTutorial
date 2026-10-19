package prep.concurrency;

/**
 * EXERCISE: sum a big array using an ExecutorService.
 * Split the array into `threads` chunks, submit a Callable<Long> per chunk, combine the Futures.
 * Always shut the pool down (try/finally). Result must equal the sequential sum.
 */
public class ParallelSum {
    public static long sum(int[] data, int threads) throws Exception {
        throw new UnsupportedOperationException("TODO");
    }
}
