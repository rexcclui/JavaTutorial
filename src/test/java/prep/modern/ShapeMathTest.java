package prep.modern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;
import prep.modern.ShapeMath.*;

class ShapeMathTest {
    // PURPOSE: Area for each sealed Shape subtype using a pattern-matching switch.
    @Test
    void areas() {
        assertThat(ShapeMath.area(new Circle(1))).isCloseTo(Math.PI, within(1e-9));
        assertThat(ShapeMath.area(new Rect(3, 4))).isEqualTo(12.0);
        assertThat(ShapeMath.area(new Triangle(3, 4))).isEqualTo(6.0);
    }

    // PURPOSE: describe() uses a guard (when) so a square is reported differently from a general rectangle.
    @Test
    void descriptions() {
        assertThat(ShapeMath.describe(new Circle(2))).isEqualTo("circle r=2.0");
        assertThat(ShapeMath.describe(new Rect(3, 4))).isEqualTo("rect 3.0x4.0");
        assertThat(ShapeMath.describe(new Rect(3, 3))).isEqualTo("square 3.0");
        assertThat(ShapeMath.describe(new Triangle(3, 4))).isEqualTo("triangle b=3.0 h=4.0");
    }

    // PURPOSE: Records auto-generate equals() and toString(); equal fields mean equal objects.
    @Test
    void recordsGiveValueEquality() {
        assertThat(new Circle(1)).isEqualTo(new Circle(1));
        assertThat(new Rect(1, 2).toString()).isEqualTo("Rect[width=1.0, height=2.0]");
    }
}
