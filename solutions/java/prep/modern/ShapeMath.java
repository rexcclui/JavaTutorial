package prep.modern;

/**
 * Java 17-21 features together: a sealed interface, records and pattern-matching switch.
 *
 * How it works:
 *   1. 'sealed ... permits' lists every allowed Shape, so the compiler knows the full set.
 *   2. Records (Circle, Rect, Triangle) are immutable data carriers with generated equals/hashCode/toString.
 *   3. A switch over a sealed type must cover every case, so no 'default' branch is needed.
 */
public class ShapeMath {
    public sealed interface Shape permits Circle, Rect, Triangle {}
    public record Circle(double radius) implements Shape {}
    public record Rect(double width, double height) implements Shape {}
    public record Triangle(double base, double height) implements Shape {}

    /**
     * Area of any shape.
     *
     * How it works:
     *   1. The switch picks the branch matching the actual type and gives it a typed variable (c, r or t).
     *   2. Circle: pi * r * r.  Rect: width * height.  Triangle: 0.5 * base * height.
     *
     * Example: new Rect(3, 4)  ->  12.0
     *
     * Complexity: O(1)
     */
    public static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rect r -> r.width() * r.height();
            case Triangle t -> 0.5 * t.base() * t.height();
        };
    }

    /**
     * Human-readable description of a shape.
     *
     * How it works:
     *   1. Circle gives "circle r=..." and Triangle gives "triangle b=... h=...".
     *   2. A Rect whose width equals its height matches the guard 'when r.width() == r.height()' and is called a square.
     *   3. Any other Rect gives "rect WxH". The guarded case must come before the general Rect case.
     *
     * Example: new Rect(3, 3)  ->  "square 3.0"
     *
     * Complexity: O(1)
     */
    public static String describe(Shape shape) {
        return switch (shape) {
            case Circle c -> "circle r=" + c.radius();
            case Rect r when r.width() == r.height() -> "square " + r.width();
            case Rect r -> "rect " + r.width() + "x" + r.height();
            case Triangle t -> "triangle b=" + t.base() + " h=" + t.height();
        };
        // TWIST: add `record Square(double side) implements Shape {}` to the permits list and
        //        watch the compiler flag every switch that is now non-exhaustive.
    }
}
