package prep.design;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

/**
 * A small parking lot: it hands out the lowest free slot number and tracks which car is where.
 *
 * How it works:
 *   1. TreeSet 'free' keeps free slot numbers sorted, so pollFirst() gives the lowest in O(log n).
 *   2. HashMap 'byPlate' maps a license plate to its slot, so lookup and removal are O(1).
 */
public class ParkingLot {
    private final TreeSet<Integer> free = new TreeSet<>();
    private final Map<String, Integer> byPlate = new HashMap<>();

    /**
     * Creates a lot with slots 1..capacity, all free.
     *
     * Complexity: O(capacity)
     */
    public ParkingLot(int capacity) {
        for (int i = 1; i <= capacity; i++) free.add(i);
    }

    /**
     * Parks a car in the lowest free slot.
     *
     * How it works:
     *   1. If the plate is already parked, throw IllegalStateException.
     *   2. If no slot is free, return empty.
     *   3. Take the lowest free slot, record the plate -> slot mapping and return the slot.
     *
     * Complexity: O(log n)
     */
    public Optional<Integer> park(String plate) {
        if (byPlate.containsKey(plate)) throw new IllegalStateException(plate + " already parked");
        if (free.isEmpty()) return Optional.empty();
        int slot = free.pollFirst();
        byPlate.put(plate, slot);
        return Optional.of(slot);
    }

    /**
     * Removes a car and frees its slot.
     *
     * How it works:
     *   1. Remove the plate from the map; if it was not parked return empty.
     *   2. Put the slot back in the free set and return it.
     *
     * Complexity: O(log n)
     */
    public Optional<Integer> leave(String plate) {
        Integer slot = byPlate.remove(plate);
        if (slot == null) return Optional.empty();
        free.add(slot);
        return Optional.of(slot);
    }

    /**
     * True when no free slots remain.
     *
     * Complexity: O(1)
     */
    public boolean isFull() { return free.isEmpty(); }

    /**
     * The slot where this plate is parked, or empty if it is not parked.
     *
     * Complexity: O(1)
     */
    public Optional<Integer> slotOf(String plate) { return Optional.ofNullable(byPlate.get(plate)); }
    // TWIST: add vehicle types (motorbike/car/truck) with different slot sizes. Which pattern fits? (Strategy? enum?)
}
