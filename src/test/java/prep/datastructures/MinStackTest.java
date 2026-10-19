package prep.datastructures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MinStackTest {
    @Test
    void tracksMinimumThroughPushAndPop() {
        MinStack s = new MinStack();
        s.push(5); s.push(3); s.push(7); s.push(3); s.push(1);
        assertThat(s.min()).isEqualTo(1);
        assertThat(s.pop()).isEqualTo(1);
        assertThat(s.min()).isEqualTo(3);
        assertThat(s.pop()).isEqualTo(3);
        assertThat(s.min()).isEqualTo(3);   // duplicate min must survive one pop
        s.pop(); s.pop();
        assertThat(s.min()).isEqualTo(5);
        assertThat(s.peek()).isEqualTo(5);
    }

    @Test
    void emptyThrows() {
        MinStack s = new MinStack();
        assertThat(s.isEmpty()).isTrue();
        assertThatThrownBy(s::pop).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(s::min).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(s::peek).isInstanceOf(IllegalStateException.class);
    }
}
