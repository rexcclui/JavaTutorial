package prep.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PointTest {
    // PURPOSE: Two separate Point objects with the same x,y must be equal, and different coordinates or null must not be. Checks that equals() compares values, not identity.
    @Test
    void equalPointsAreEqual() {
        assertThat(new Point(1, 2)).isEqualTo(new Point(1, 2));
        assertThat(new Point(1, 2)).isNotEqualTo(new Point(2, 1));
        assertThat(new Point(1, 2)).isNotEqualTo(null);
    }

    // PURPOSE: The equals/hashCode contract: equal objects must produce the same hashCode, or hash collections break.
    @Test
    void equalPointsHaveEqualHashCodes() {
        assertThat(new Point(3, 4).hashCode()).isEqualTo(new Point(3, 4).hashCode());
    }

    // PURPOSE: Practical effect of the contract: adding the same logical point twice to a HashSet keeps only one.
    @Test
    void worksInHashSet() {
        Set<Point> set = new HashSet<>();
        set.add(new Point(0, 0));
        set.add(new Point(0, 0));
        assertThat(set).hasSize(1);
    }

    // PURPOSE: Practical effect of the contract: a map lookup with a NEW but equal Point finds the value stored earlier.
    @Test
    void worksAsHashMapKey() {
        Map<Point, String> map = new HashMap<>();
        map.put(new Point(5, 6), "a");
        assertThat(map.get(new Point(5, 6))).isEqualTo("a");
    }

    // PURPOSE: toString() should give a readable "(x, y)" instead of the default ClassName@hash.
    @Test
    void toStringFormat() {
        assertThat(new Point(1, -2).toString()).isEqualTo("(1, -2)");
    }

    // YOUR EDGE CASES HERE
}
