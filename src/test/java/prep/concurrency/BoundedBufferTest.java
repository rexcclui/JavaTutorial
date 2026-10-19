package prep.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class BoundedBufferTest {
    @Test
    @Timeout(10)
    void fifoOrderSingleThread() throws Exception {
        BoundedBuffer<Integer> b = new BoundedBuffer<>(3);
        b.put(1);
        b.put(2);
        assertThat(b.take()).isEqualTo(1);
        assertThat(b.take()).isEqualTo(2);
        assertThat(b.size()).isEqualTo(0);
    }

    @Test
    @Timeout(10)
    void takeBlocksUntilItemArrives() throws Exception {
        BoundedBuffer<String> b = new BoundedBuffer<>(1);
        ConcurrentLinkedQueue<String> got = new ConcurrentLinkedQueue<>();
        Thread consumer = prep.TestThreads.newThread(() -> {
            try { got.add(b.take()); } catch (InterruptedException ignored) { }
        });
        consumer.start();
        TimeUnit.MILLISECONDS.sleep(100);
        assertThat(got).isEmpty();             // still blocked
        b.put("hello");
        consumer.join(2000);
        assertThat(got).containsExactly("hello");
    }

    @Test
    @Timeout(10)
    void putBlocksWhenFull() throws Exception {
        BoundedBuffer<Integer> b = new BoundedBuffer<>(1);
        b.put(1);
        Thread producer = prep.TestThreads.newThread(() -> {
            try { b.put(2); } catch (InterruptedException ignored) { }
        });
        producer.start();
        TimeUnit.MILLISECONDS.sleep(100);
        assertThat(producer.isAlive()).isTrue();   // blocked on full buffer
        assertThat(b.take()).isEqualTo(1);
        producer.join(2000);
        assertThat(b.take()).isEqualTo(2);
    }

    @Test
    @Timeout(20)
    void manyProducersManyConsumers() throws Exception {
        BoundedBuffer<Integer> b = new BoundedBuffer<>(4);
        int producers = 4, consumers = 4, perProducer = 5_000;
        java.util.concurrent.atomic.AtomicLong sum = new java.util.concurrent.atomic.AtomicLong();
        Thread[] ps = new Thread[producers], cs = new Thread[consumers];
        for (int i = 0; i < producers; i++) {
            ps[i] = prep.TestThreads.newThread(() -> {
                try { for (int j = 1; j <= perProducer; j++) b.put(j); } catch (InterruptedException ignored) { }
            });
            ps[i].start();
        }
        for (int i = 0; i < consumers; i++) {
            cs[i] = prep.TestThreads.newThread(() -> {
                try { for (int j = 0; j < perProducer; j++) sum.addAndGet(b.take()); } catch (InterruptedException ignored) { }
            });
            cs[i].start();
        }
        for (Thread t : ps) t.join();
        for (Thread t : cs) t.join();
        long expected = (long) producers * perProducer * (perProducer + 1) / 2;
        assertThat(sum.get()).isEqualTo(expected);
    }
}
