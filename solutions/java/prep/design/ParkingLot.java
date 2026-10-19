package prep.design;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

public class ParkingLot {
    private final TreeSet<Integer> free = new TreeSet<>();
    private final Map<String, Integer> bySlot = new HashMap<>();

    public ParkingLot(int capacity) {
        for (int i = 1; i <= capacity; i++) free.add(i);
    }

    public Optional<Integer> park(String plate) {
        if (bySlot.containsKey(plate)) throw new IllegalStateException(plate + " already parked");
        if (free.isEmpty()) return Optional.empty();
        int slot = free.pollFirst();
        bySlot.put(plate, slot);
        return Optional.of(slot);
    }

    public Optional<Integer> leave(String plate) {
        Integer slot = bySlot.remove(plate);
        if (slot == null) return Optional.empty();
        free.add(slot);
        return Optional.of(slot);
    }

    public boolean isFull() { return free.isEmpty(); }

    public Optional<Integer> slotOf(String plate) { return Optional.ofNullable(bySlot.get(plate)); }
    // TWIST: add vehicle types (motorbike/car/truck) with different slot sizes. Which pattern fits? (Strategy? enum?)
}
