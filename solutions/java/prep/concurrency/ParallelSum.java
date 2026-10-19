package prep.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Sums a large int array using several threads.
 */
public class ParallelSum {
    /**
     * Splits the array into equal chunks, sums each chunk on its own thread, then adds the partial results.
     *
     * How it works:
     *   1. Create a fixed thread pool with 'threads' worker threads.
     *   2. chunk = ceil(length / threads); for each chunk submit a Callable that adds up its slice into a long (long avoids int overflow).
     *   3. Each submit returns a Future; call get() on each Future to wait for it and add its partial sum to the total.
     *   4. shutdown() the pool in a finally block so threads are always released, even after an exception.
     *
     * Example: {1,2,3,4,5,6}, 2 threads  ->  chunks [1,2,3] and [4,5,6]  ->  6 + 15 = 21
     *
     * Complexity: time O(n / threads) with enough cores, space O(threads)
     */
    public static long sum(int[] data, int threads) throws Exception {
        if (threads <= 0) throw new IllegalArgumentException("threads must be > 0");
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            int chunk = (data.length + threads - 1) / threads;
            List<Future<Long>> parts = new ArrayList<>();
            for (int start = 0; start < data.length; start += chunk) {
                int from = start, to = Math.min(data.length, start + chunk);
                Callable<Long> task = () -> {
                    long s = 0;
                    for (int i = from; i < to; i++) s += data[i];
                    return s;
                };
                parts.add(pool.submit(task));
            }
            long total = 0;
            for (Future<Long> f : parts) total += f.get();
            return total;
        } finally {
            pool.shutdown();
        }
        // TWIST: rewrite with CompletableFuture.supplyAsync(...) and allOf(...), or with Arrays.stream(data).parallel().
    }
}
