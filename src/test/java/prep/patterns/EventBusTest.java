package prep.patterns;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class EventBusTest {
    @Test
    void deliversToTopicSubscribersInOrder() {
        EventBus bus = new EventBus();
        List<String> log = new ArrayList<>();
        bus.subscribe("news", m -> log.add("A:" + m));
        bus.subscribe("news", m -> log.add("B:" + m));
        bus.subscribe("other", m -> log.add("X:" + m));
        bus.publish("news", "hi");
        assertThat(log).containsExactly("A:hi", "B:hi");
    }

    @Test
    void unsubscribeStopsDelivery() {
        EventBus bus = new EventBus();
        List<String> log = new ArrayList<>();
        Runnable off = bus.subscribe("t", log::add);
        bus.publish("t", "1");
        off.run();
        bus.publish("t", "2");
        assertThat(log).containsExactly("1");
    }

    @Test
    void failingListenerDoesNotBlockOthers() {
        EventBus bus = new EventBus();
        List<String> log = new ArrayList<>();
        bus.subscribe("t", m -> { throw new IllegalStateException("boom"); });
        bus.subscribe("t", log::add);
        bus.publish("t", "ok");
        assertThat(log).containsExactly("ok");
    }

    @Test
    void listenerMayUnsubscribeItself() {
        EventBus bus = new EventBus();
        List<String> log = new ArrayList<>();
        Runnable[] off = new Runnable[1];
        off[0] = bus.subscribe("t", m -> { log.add(m); off[0].run(); });
        bus.subscribe("t", m -> log.add("second:" + m));
        bus.publish("t", "1");
        bus.publish("t", "2");
        assertThat(log).containsExactly("1", "second:1", "second:2");
    }

    @Test
    void publishToUnknownTopicIsNoOp() {
        new EventBus().publish("nobody", "hello");
    }
}
