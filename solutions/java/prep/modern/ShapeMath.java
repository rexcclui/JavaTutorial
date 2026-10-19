package prep.modern;

public class ShapeMath {
    public sealed interface Shape permits Circle, Rect, Triangle {}
    public record Circle(double radius) implements Shape {}
    public record Rect(double width, double height) implements Shape {}
    public record Triangle(double base, double height) implements Shape {}

    public static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rect r -> r.width() * r.height();
            case Triangle t -> 0.5 * t.base() * t.height();
        };
    }

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
