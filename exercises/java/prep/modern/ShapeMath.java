package prep.modern;

/**
 * EXERCISE: Java 17-21 features.
 * Implement area() and describe() using a pattern-matching switch over the sealed hierarchy.
 * No instanceof chains, no default branch (the compiler should prove exhaustiveness).
 */
public class ShapeMath {
    public sealed interface Shape permits Circle, Rect, Triangle {}
    public record Circle(double radius) implements Shape {}
    public record Rect(double width, double height) implements Shape {}
    public record Triangle(double base, double height) implements Shape {}

    public static double area(Shape shape) {
        throw new UnsupportedOperationException("TODO");
    }

    /** "circle r=2.0", "rect 3.0x4.0", "triangle b=3.0 h=4.0". Squares (rect with w==h) -> "square 3.0". */
    public static String describe(Shape shape) {
        throw new UnsupportedOperationException("TODO");
    }
}
