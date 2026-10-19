package prep.collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class LruCacheTest {
    @Test
    void getMissReturnsNull() {
        assertThat(new LruCache<String, Integer>(2).get("x")).isNull();
    }

    @Test
    void evictsLeastRecentlyUsed() {
        LruCache<String, Integer> c = new LruCache<>(2);
        c.put("a", 1);
        c.put("b", 2);
        c.put("c", 3);               // evicts a
        assertThat(c.get("a")).isNull();
        assertThat(c.get("b")).isEqualTo(2);
        assertThat(c.get("c")).isEqualTo(3);
        assertThat(c.size()).isEqualTo(2);
    }

    @Test
    void getRefreshesRecency() {
        LruCache<String, Integer> c = new LruCache<>(2);
        c.put("a", 1);
        c.put("b", 2);
        c.get("a");                  // a is now most recent
        c.put("c", 3);               // evicts b
        assertThat(c.get("b")).isNull();
        assertThat(c.get("a")).isEqualTo(1);
    }

    @Test
    void putUpdatesExistingKey() {
        LruCache<String, Integer> c = new LruCache<>(2);
        c.put("a", 1);
        c.put("a", 9);
        assertThat(c.get("a")).isEqualTo(9);
        assertThat(c.size()).isEqualTo(1);
    }

    @Test
    void rejectsNonPositiveCapacity() {
        assertThatThrownBy(() -> new LruCache<String, String>(0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Disabled("Level 2: make LruCache thread-safe, write a multi-thread test, then remove @Disabled")
    @Test
    void level2ThreadSafe() {
    }

    // YOUR EDGE CASES HERE
}
