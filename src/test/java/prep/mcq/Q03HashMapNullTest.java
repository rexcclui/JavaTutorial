package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 3 - null in a HashMap
 *
 * Which statement about java.util.HashMap and null is correct?
 *
 *   A. It permits one null key and any number of null values.
 *   B. It permits neither null keys nor null values.
 *   C. It permits any number of null keys but no null values.
 *   D. get(null) always throws NullPointerException.
 */
class Q03HashMapNullTest {
    /** Your answer: 'A', 'B', 'C' or 'D'. Leave '?' to skip. */
    private static final char MY_ANSWER = '?';
    private static final char CORRECT = 'A';

    // PURPOSE: Check your answer. Skipped until you set MY_ANSWER; fails if it is wrong.
    @Test
    void checkMyAnswer() {
        McqSupport.check(MY_ANSWER, CORRECT);
    }

    // PURPOSE: Concept - try a null key and several null values in a HashMap.
    @Test
    void concept_nullKeyAndNullValues() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "for-null-key");
        map.put("a", null);
        map.put("b", null);
        assertThat(map).hasSize(3);
        assertThat(map.get(null)).isEqualTo("for-null-key");
        assertThat(map.containsKey("a")).isTrue();     // key present, value null
        assertThat(map.get("a")).isNull();             // so get() == null does NOT mean "key missing"; use containsKey
    }

    // PURPOSE: Experiment for option B - does HashMap reject null keys or null values?
    @Test
    void experimentB_nullsAreAccepted() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "v");
        map.put("k", null);
        assertThat(map).containsEntry(null, "v").containsEntry("k", null);
        // Verdict: B is false for HashMap. (It is TRUE for Hashtable and ConcurrentHashMap - see the contrast test below.)
    }

    // PURPOSE: Experiment for option C - how many null keys can there be? A second put(null, ...) replaces the first.
    @Test
    void experimentC_onlyOneNullKey() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "first");
        map.put(null, "second");
        assertThat(map).hasSize(1);
        assertThat(map.get(null)).isEqualTo("second");
        // Verdict: C is false. Keys are unique, so there can be only one null key.
    }

    // PURPOSE: Experiment for option D - get(null) on a HashMap simply looks up the null key.
    @Test
    void experimentD_getNullDoesNotThrow() {
        assertThat(new HashMap<String, String>().get(null)).isNull();
        // Verdict: D is false for HashMap.
    }

    // PURPOSE: Contrast - other maps behave differently: ConcurrentHashMap and Hashtable reject nulls, TreeMap rejects a null key.
    @Test
    void contrast_otherMapsRejectNulls() {
        assertThatThrownBy(() -> new ConcurrentHashMap<String, String>().put(null, "v")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ConcurrentHashMap<String, String>().put("k", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Hashtable<String, String>().put("k", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new TreeMap<String, String>().put(null, "v")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Map.of("k", null)).isInstanceOf(NullPointerException.class);   // immutable Map.of also rejects nulls
    }
}
