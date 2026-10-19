package prep.design;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ParkingLotTest {
    // PURPOSE: Slots are assigned 1,2,3... lowest first, and a full lot returns empty.
    @Test
    void assignsLowestFreeSlot() {
        ParkingLot lot = new ParkingLot(3);
        assertThat(lot.park("A")).isEqualTo(Optional.of(1));
        assertThat(lot.park("B")).isEqualTo(Optional.of(2));
        assertThat(lot.park("C")).isEqualTo(Optional.of(3));
        assertThat(lot.isFull()).isTrue();
        assertThat(lot.park("D")).isEmpty();
    }

    // PURPOSE: After cars leave, the lowest free slot number is reused first.
    @Test
    void reusesFreedSlotsLowestFirst() {
        ParkingLot lot = new ParkingLot(3);
        lot.park("A"); lot.park("B"); lot.park("C");
        assertThat(lot.leave("C")).isEqualTo(Optional.of(3));
        assertThat(lot.leave("A")).isEqualTo(Optional.of(1));
        assertThat(lot.park("D")).isEqualTo(Optional.of(1));
        assertThat(lot.slotOf("D")).isEqualTo(Optional.of(1));
        assertThat(lot.slotOf("A")).isEmpty();
    }

    // PURPOSE: Removing a car that is not parked returns empty instead of throwing.
    @Test
    void leaveUnknownPlate() {
        assertThat(new ParkingLot(1).leave("ghost")).isEmpty();
    }

    // PURPOSE: The same plate cannot be parked twice (IllegalStateException).
    @Test
    void duplicatePlateRejected() {
        ParkingLot lot = new ParkingLot(2);
        lot.park("A");
        assertThatThrownBy(() -> lot.park("A")).isInstanceOf(IllegalStateException.class);
    }
}
