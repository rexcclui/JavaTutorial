package prep.patterns;

import java.util.ArrayList;
import java.util.List;

public final class Pizza {
    private final int size;
    private final boolean cheese;
    private final List<String> toppings;

    private Pizza(Builder b) {
        this.size = b.size;
        this.cheese = b.cheese;
        this.toppings = List.copyOf(b.toppings);   // defensive copy, unmodifiable
    }

    public static Builder builder(int size) {
        return new Builder(size);
    }

    public int size() { return size; }
    public boolean cheese() { return cheese; }
    public List<String> toppings() { return toppings; }

    public static class Builder {
        private final int size;
        private boolean cheese = true;
        private final List<String> toppings = new ArrayList<>();

        private Builder(int size) { this.size = size; }

        public Builder cheese(boolean cheese) { this.cheese = cheese; return this; }
        public Builder topping(String topping) { this.toppings.add(topping); return this; }

        public Pizza build() {
            if (size < 8 || size > 18) throw new IllegalArgumentException("size must be 8..18, was " + size);
            return new Pizza(this);
        }
    }
}
