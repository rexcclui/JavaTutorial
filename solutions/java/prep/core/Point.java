package prep.core;

import java.util.Objects;

/** Reference solution: immutable value class with the equals/hashCode contract. */
public final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point p)) return false;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    // TWIST: delete hashCode() and rerun PointTest -> which tests fail, and why?
    // TWIST: make the class non-final and subclass it -> what breaks in equals symmetry?
}
