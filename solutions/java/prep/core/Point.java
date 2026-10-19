package prep.core;

import java.util.Objects;

/**
 * An immutable 2D point that can safely be used as a HashMap key or stored in a HashSet.
 *
 * How it works:
 *   1. Fields are final and the class is final, so a Point can never change after creation.
 *   2. equals() and hashCode() both use x and y, so two Points with the same coordinates are interchangeable.
 */
public final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate.
     *
     * Complexity: O(1)
     */
    public int x() { return x; }
    /**
     * Returns the y coordinate.
     *
     * Complexity: O(1)
     */
    public int y() { return y; }

    /**
     * Two Points are equal when they have the same x and the same y (value equality, not identity).
     *
     * How it works:
     *   1. If it is the very same object, return true right away (cheap shortcut).
     *   2. If the other object is not a Point (this also covers null), return false.
     *   3. Otherwise compare x and y.
     *
     * Example: new Point(1, 2).equals(new Point(1, 2))  ->  true
     *
     * Complexity: O(1)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point p)) return false;
        return x == p.x && y == p.y;
    }

    /**
     * Computes a hash code from x and y. Required whenever equals() is overridden: equal points MUST give the same hash code.
     *
     * How it works:
     *   1. Objects.hash(x, y) combines the fields as 31 * (31 * 1 + x) + y.
     *   2. Order matters, so (1, 2) and (2, 1) get different hashes.
     *
     * Example: new Point(1, 2).hashCode()  ->  994
     *
     * Complexity: O(1)
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Readable text form of the point.
     *
     * Example: new Point(1, -2).toString()  ->  "(1, -2)"
     *
     * Complexity: O(1)
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    // TWIST: delete hashCode() and rerun PointTest -> which tests fail, and why?
    // TWIST: make the class non-final and subclass it -> what breaks in equals symmetry?
}
