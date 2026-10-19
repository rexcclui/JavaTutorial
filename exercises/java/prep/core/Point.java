package prep.core;

/**
 * EXERCISE: make this an immutable value class.
 * - fields must be final, no setters
 * - equals/hashCode must be consistent so Points work as HashMap keys / in HashSets
 * - toString returns "(x, y)"
 * Hint: see docs/core.md -> "equals and hashCode contract".
 */
public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    // TODO: equals, hashCode, toString
}
