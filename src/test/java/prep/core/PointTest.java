package prep.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PointTest {
    @Test
    void equalPointsAreEqual() {
        assertThat(new Point(1, 2)).isEqualTo(new Point(1, 2));
        assertThat(new Point(1, 2)).isNotEqualTo(new Point(2, 1));
        assertThat(new Point(1, 2)).isNotEqualTo(null);
    }

    @Test
    void equalPointsHaveEqualHashCodes() {
        assertThat(new Point(3, 4).hashCode()).isEqualTo(new Point(3, 4).hashCode());
    }

    @Test
    void worksInHashSet() {
        Set<Point> set = new HashSet<>();
        set.add(new Point(0, 0));
        set.add(new Point(0, 0));
        assertThat(set).hasSize(1);
    }

    @Test
    void worksAsHashMapKey() {
        Map<Point, String> map = new HashMap<>();
        map.put(new Point(5, 6), "a");
        assertThat(map.get(new Point(5, 6))).isEqualTo("a");
    }

    @Test
    void toStringFormat() {
        assertThat(new Point(1, -2).toString()).isEqualTo("(1, -2)");
    }

    // YOUR EDGE CASES HERE
}
