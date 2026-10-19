package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

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
 *
 * ANSWER: A
 *
 * WHY:
 *   A. Correct - one null key (keys are unique) and as many null values as you like.
 *   B. Wrong - that describes Hashtable and ConcurrentHashMap, not HashMap.
 *   C. Wrong - a second put(null, ...) just replaces the first, because keys are unique.
 *   D. Wrong - get(null) simply looks up the null key.
 *
 * See it run:  ./run.sh mcq Q03
 */
class Q03HashMapNullTest {

    // PURPOSE: Demo - put a null key and null values into a HashMap and read them back.
    @Test
    void demo_nullKeyAndNullValues() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "for-null-key");
        map.put("a", null);
        map.put("b", null);
        McqSupport.show("map                = %s", map);
        McqSupport.show("map.get(null)      = %s", map.get(null));
        McqSupport.show("map.get(\"a\")       = %s   (key exists, value is null)", map.get("a"));
        McqSupport.show("map.get(\"zzz\")     = %s   (key missing: ALSO null!)", map.get("zzz"));
        McqSupport.show("containsKey(\"a\")   = %s,  containsKey(\"zzz\") = %s   <- use this to tell them apart", map.containsKey("a"), map.containsKey("zzz"));
        assertThat(map).hasSize(3);
        assertThat(map.get(null)).isEqualTo("for-null-key");
        assertThat(map.containsKey("a")).isTrue();
        assertThat(map.get("a")).isNull();
    }

    // PURPOSE: Option B is WRONG - HashMap accepts null keys and null values.
    @Test
    void optionB_wrong_nullsAreAccepted() {
        Map<String, String> map = new HashMap<>();
        McqSupport.attempt("put(null, \"v\")", () -> map.put(null, "v"));
        McqSupport.attempt("put(\"k\", null)", () -> map.put("k", null));
        assertThat(map).containsEntry(null, "v").containsEntry("k", null);
    }

    // PURPOSE: Option C is WRONG - only ONE null key can exist; a second put(null, ...) overwrites.
    @Test
    void optionC_wrong_onlyOneNullKey() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "first");
        map.put(null, "second");
        McqSupport.show("after put(null,\"first\") and put(null,\"second\"): %s  (size %d)", map, map.size());
        assertThat(map).hasSize(1);
        assertThat(map.get(null)).isEqualTo("second");
    }

    // PURPOSE: Option D is WRONG - get(null) on a HashMap just returns null (or the value stored for the null key).
    @Test
    void optionD_wrong_getNullDoesNotThrow() {
        McqSupport.attempt("new HashMap<>().get(null)", () -> new HashMap<String, String>().get(null));
        assertThat(new HashMap<String, String>().get(null)).isNull();
    }

    // PURPOSE: Contrast - other maps DO reject nulls (this is what option B was confusing HashMap with).
    @Test
    void contrast_otherMapsRejectNulls() {
        McqSupport.attempt("ConcurrentHashMap.put(null, v)", () -> new ConcurrentHashMap<String, String>().put(null, "v"));
        McqSupport.attempt("ConcurrentHashMap.put(k, null)", () -> new ConcurrentHashMap<String, String>().put("k", null));
        McqSupport.attempt("Hashtable.put(k, null)", () -> new Hashtable<String, String>().put("k", null));
        McqSupport.attempt("TreeMap.put(null, v)", () -> new TreeMap<String, String>().put(null, "v"));
        McqSupport.attempt("Map.of(k, null)", () -> Map.of("k", null));
        McqSupport.attempt("HashMap.put(null, v)", () -> new HashMap<String, String>().put(null, "v"));
    }
}
