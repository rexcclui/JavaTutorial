package prep.patterns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PizzaTest {
    // PURPOSE: Builder: optional fields get sensible defaults (cheese on, no toppings).
    @Test
    void defaults() {
        Pizza p = Pizza.builder(12).build();
        assertThat(p.size()).isEqualTo(12);
        assertThat(p.cheese()).isTrue();
        assertThat(p.toppings()).isEmpty();
    }

    // PURPOSE: Builder: chained setters configure the object.
    @Test
    void customised() {
        Pizza p = Pizza.builder(14).cheese(false).topping("olives").topping("ham").build();
        assertThat(p.cheese()).isFalse();
        assertThat(p.toppings()).containsExactly("olives", "ham");
    }

    // PURPOSE: Builder: build() validates input and rejects a size outside 8..18.
    @Test
    void validatesSize() {
        assertThatThrownBy(() -> Pizza.builder(5).build()).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Pizza.builder(30).build()).isInstanceOf(IllegalArgumentException.class);
    }

    // PURPOSE: Immutability: changing the builder afterwards or modifying the returned list must not change the built Pizza (defensive copy).
    @Test
    void toppingsAreImmutableAndIsolatedFromBuilder() {
        Pizza.Builder b = Pizza.builder(10).topping("a");
        Pizza p = b.build();
        b.topping("b");
        assertThat(p.toppings()).containsExactly("a");
        assertThatThrownBy(() -> p.toppings().add("x")).isInstanceOf(UnsupportedOperationException.class);
    }
}
