package prep.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelSum {
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
