package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * QUESTION 19 - Integer cache
 *
 *     Integer a = 127, b = 127, c = 128, d = 128;
 *     System.out.println((a == b) + " " + (c == d));
 *
 * What does it print?
 *
 *   A. true true
 *   B. true false
 *   C. false false
 *   D. false true
 *
 * ANSWER: B
 *
 * WHY:
 *   Autoboxing calls Integer.valueOf(int). It returns a cached, shared object for -128..127, and a NEW object otherwise.
 *   a == b : same cached object  -> true.     c == d : two different objects -> false.
 *   == on objects compares references. Use equals() to compare Integer values.
 *
 * See it run:  ./run.sh mcq Q19
 */
@SuppressWarnings("NumberEquality")
class Q19IntegerCacheTest {

    // PURPOSE: Demo - the identity of each boxed Integer: same number = same object.
    @Test
    void demo_identityOfBoxedIntegers() {
        Integer a = 127, b = 127, c = 128, d = 128;
        McqSupport.show("a (127) identity = %d", System.identityHashCode(a));
        McqSupport.show("b (127) identity = %d   a == b ? %s   <- the same cached object", System.identityHashCode(b), a == b);
        McqSupport.show("c (128) identity = %d", System.identityHashCode(c));
        McqSupport.show("d (128) identity = %d   c == d ? %s   <- two different objects", System.identityHashCode(d), c == d);
        McqSupport.show("c.equals(d) = %s   <- compares the VALUES", c.equals(d));
        assertThat((a == b) + " " + (c == d)).isEqualTo("true false");
    }

    // PURPOSE: Find where the cache ends: scan up from -128 and print the first number whose two boxes differ (128), and check the lower edge too.
    @Test
    void demo_whereTheCacheEnds() {
        int firstNotCachedAbove = Integer.MIN_VALUE;
        for (int i = -128; i < 1000; i++) {
            Integer x = i, y = i;
            if (x != y) { firstNotCachedAbove = i; break; }
        }
        Integer lowEdgeA = -128, lowEdgeB = -128, belowA = -129, belowB = -129;
        McqSupport.show("scanning up from -128: first number whose two boxes are different objects = %d", firstNotCachedAbove);
        McqSupport.show("-128 == -128 : %s    -129 == -129 : %s   <- cache covers -128..127", lowEdgeA == lowEdgeB, belowA == belowB);
        assertThat(firstNotCachedAbove).isEqualTo(128);
        assertThat(lowEdgeA == lowEdgeB).isTrue();
        assertThat(belowA == belowB).isFalse();
    }

    // PURPOSE: Why this is not a bug to rely on: new Integer values via valueOf above 127 are always fresh objects.
    @Test
    void why_valueOfCachesOnlyTheSmallRange() {
        assertThat(Integer.valueOf(127) == Integer.valueOf(127)).isTrue();
        assertThat(Integer.valueOf(128) == Integer.valueOf(128)).isFalse();
        McqSupport.show("Integer.valueOf(127) == Integer.valueOf(127) -> true;  Integer.valueOf(128) == Integer.valueOf(128) -> false");
    }

    // PURPOSE: The fix - compare wrapper values with equals(), or unbox one side so == compares numbers.
    @Test
    void fix_useEqualsOrUnbox() {
        Integer c = 128, d = 128;
        McqSupport.show("c.equals(d) = %s,   c == 128 (one side unboxed) = %s,   c.intValue() == d.intValue() = %s", c.equals(d), c == 128, c.intValue() == d.intValue());
        assertThat(c.equals(d)).isTrue();
        assertThat(c == 128).isTrue();
    }
}
