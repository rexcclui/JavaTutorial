package prep.mcq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

/**
 * QUESTION 26 - map iteration order
 *
 * Which Map implementation iterates its entries in the order they were inserted?
 *
 *   A. HashMap
 *   B. TreeMap
 *   C. Hashtable
 *   D. LinkedHashMap
 *
 * ANSWER: D
 *
 * WHY:
 *   A. HashMap - no guaranteed order (it depends on hash codes and table size, and can change after a resize).
 *   B. TreeMap - sorted by key (natural order or a Comparator), not by insertion.
 *   C. Hashtable - legacy, synchronized, no guaranteed order.
 *   D. LinkedHashMap - a hash map plus a linked list through the entries; keeps insertion order (or access order if you ask for it).
 *
 * See it run:  ./run.sh mcq Q26
 */
class Q26MapOrderingTest {
    private static final List<String> KEYS = List.of("banana", "apple", "cherry", "date", "elderberry");

    private static Map<String, Integer> fill(Map<String, Integer> map) {
        int i = 0;
        for (String k : KEYS) map.put(k, i++);
        return map;
    }

    // PURPOSE: Demo - insert the same five keys into each map and print the iteration order.
    @Test
    void demo_sameKeysDifferentOrders() {
        McqSupport.show("inserted in this order : %s", KEYS);
        McqSupport.show("HashMap                : %s", fill(new HashMap<>()).keySet());
        McqSupport.show("LinkedHashMap          : %s   <- insertion order", fill(new LinkedHashMap<>()).keySet());
        McqSupport.show("TreeMap                : %s   <- sorted by key", fill(new TreeMap<>()).keySet());
        assertThat(fill(new LinkedHashMap<>()).keySet()).containsExactlyElementsOf(KEYS);
        assertThat(fill(new TreeMap<>()).keySet()).containsExactly("apple", "banana", "cherry", "date", "elderberry");
    }

    // PURPOSE: Option A is WRONG - HashMap order is unspecified; do not rely on it (the test only checks that all keys are present).
    @Test
    void optionA_wrong_hashMapHasNoGuaranteedOrder() {
        assertThat(fill(new HashMap<>()).keySet()).containsExactlyInAnyOrderElementsOf(KEYS);
        McqSupport.show("HashMap holds the same keys, but its order is an implementation detail");
    }

    // PURPOSE: Option B is WRONG - TreeMap sorts keys; insertion order is lost.
    @Test
    void optionB_wrong_treeMapSortsKeys() {
        var keys = new java.util.ArrayList<>(fill(new TreeMap<>()).keySet());
        McqSupport.show("TreeMap key order %s differs from the insertion order %s", keys, KEYS);
        assertThat(keys).isNotEqualTo(KEYS).isSorted();
    }

    // PURPOSE: Option D is CORRECT - LinkedHashMap keeps insertion order, and re-putting an existing key does NOT move it.
    @Test
    void optionD_correct_linkedHashMapKeepsInsertionOrder() {
        Map<String, Integer> map = fill(new LinkedHashMap<>());
        map.put("banana", 99);
        McqSupport.show("after map.put(\"banana\", 99) again: %s   <- still first", map);
        assertThat(map.keySet()).containsExactlyElementsOf(KEYS);
    }

    // PURPOSE: Bonus - LinkedHashMap(accessOrder = true) orders by last ACCESS; this is the trick behind a simple LRU cache.
    @Test
    void bonus_accessOrderMode() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>(16, 0.75f, true);
        fill(map);
        map.get("banana");                                    // touching banana moves it to the end
        McqSupport.show("after map.get(\"banana\") in access-order mode: %s", map.keySet());
        assertThat(map.keySet()).containsExactly("apple", "cherry", "date", "elderberry", "banana");
    }
}
