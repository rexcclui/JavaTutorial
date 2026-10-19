package prep.patterns;

import java.util.List;

/**
 * EXERCISE: Builder.
 * Pizza is immutable. Required: size (inches, 8..18). Optional: cheese (default true), toppings (default none).
 *   Pizza p = Pizza.builder(12).cheese(false).topping("olives").topping("ham").build();
 * build() validates (size range) and throws IllegalArgumentException. toppings() must be unmodifiable.
 */
public class Pizza {
    public static Builder builder(int size) {
        throw new UnsupportedOperationException("TODO");
    }

    public int size() { throw new UnsupportedOperationException("TODO"); }
    public boolean cheese() { throw new UnsupportedOperationException("TODO"); }
    public List<String> toppings() { throw new UnsupportedOperationException("TODO"); }

    public static class Builder {
        public Builder cheese(boolean cheese) { throw new UnsupportedOperationException("TODO"); }
        public Builder topping(String topping) { throw new UnsupportedOperationException("TODO"); }
        public Pizza build() { throw new UnsupportedOperationException("TODO"); }
    }
}
