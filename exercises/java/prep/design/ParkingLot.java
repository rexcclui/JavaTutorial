package prep.design;

import java.util.Optional;

/**
 * EXERCISE: tiny parking lot (object modelling + data structure choice).
 * - Slots are numbered 1..capacity. park() assigns the LOWEST free slot.
 * - Parking a plate that is already parked -> IllegalStateException.
 * - leave(plate) frees its slot and returns it; unknown plate -> Optional.empty().
 * - Everything O(log n) or better. (Hint: TreeSet / PriorityQueue for free slots + HashMap plate->slot.)
 */
public class ParkingLot {
    public ParkingLot(int capacity) {
        // TODO
    }

    /** Slot number, or empty if the lot is full. */
    public Optional<Integer> park(String plate) {
        throw new UnsupportedOperationException("TODO");
    }

    public Optional<Integer> leave(String plate) {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isFull() {
        throw new UnsupportedOperationException("TODO");
    }

    public Optional<Integer> slotOf(String plate) {
        throw new UnsupportedOperationException("TODO");
    }
}
