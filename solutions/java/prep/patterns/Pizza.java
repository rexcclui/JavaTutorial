package prep.patterns;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder pattern: build an immutable Pizza step by step through named, chainable calls.
 *
 * How it works:
 *   1. Required value (size) goes into builder(size); optional ones have defaults and chainable setters.
 *   2. build() validates everything in one place, then creates the Pizza.
 */
public final class Pizza {
    private final int size;
    private final boolean cheese;
    private final List<String> toppings;

    /**
     * Private: only the Builder can create a Pizza. Copies the builder's values and makes the toppings list unmodifiable.
     *
     * Complexity: O(toppings)
     */
    private Pizza(Builder b) {
        this.size = b.size;
        this.cheese = b.cheese;
        this.toppings = List.copyOf(b.toppings);   // defensive copy, unmodifiable
    }

    /**
     * Starts building a pizza of the given size.
     *
     * Example: Pizza.builder(12).cheese(false).topping("ham").build()
     *
     * Complexity: O(1)
     */
    public static Builder builder(int size) {
        return new Builder(size);
    }

    /**
     * Size in inches.
     *
     * Complexity: O(1)
     */
    public int size() { return size; }
    /**
     * Whether the pizza has cheese.
     *
     * Complexity: O(1)
     */
    public boolean cheese() { return cheese; }
    /**
     * Unmodifiable list of toppings (add() throws UnsupportedOperationException).
     *
     * Complexity: O(1)
     */
    public List<String> toppings() { return toppings; }

    public static class Builder {
        private final int size;
        private boolean cheese = true;
        private final List<String> toppings = new ArrayList<>();

        private Builder(int size) { this.size = size; }

        /**
         * Sets cheese on or off and returns this builder so calls can be chained.
         *
         * Complexity: O(1)
         */
        public Builder cheese(boolean cheese) { this.cheese = cheese; return this; }
        /**
         * Adds one topping and returns this builder.
         *
         * Complexity: O(1)
         */
        public Builder topping(String topping) { this.toppings.add(topping); return this; }

        /**
         * Validates the settings and creates the immutable Pizza.
         *
         * How it works:
         *   1. Reject sizes outside 8..18 with IllegalArgumentException.
         *   2. Create the Pizza, which takes its own copy of the toppings, so later changes to the builder do not affect it.
         *
         * Complexity: O(toppings)
         */
        public Pizza build() {
            if (size < 8 || size > 18) throw new IllegalArgumentException("size must be 8..18, was " + size);
            return new Pizza(this);
        }
    }
}
