package prep.design;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ParkingLotTest {
    @Test
    void assignsLowestFreeSlot() {
        ParkingLot lot = new ParkingLot(3);
        assertThat(lot.park("A")).isEqualTo(Optional.of(1));
        assertThat(lot.park("B")).isEqualTo(Optional.of(2));
        assertThat(lot.park("C")).isEqualTo(Optional.of(3));
        assertThat(lot.isFull()).isTrue();
        assertThat(lot.park("D")).isEmpty();
    }

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

    @Test
    void leaveUnknownPlate() {
        assertThat(new ParkingLot(1).leave("ghost")).isEmpty();
    }

    @Test
    void duplicatePlateRejected() {
        ParkingLot lot = new ParkingLot(2);
        lot.park("A");
        assertThatThrownBy(() -> lot.park("A")).isInstanceOf(IllegalStateException.class);
    }
}
