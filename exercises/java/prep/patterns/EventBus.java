package prep.patterns;

import java.util.function.Consumer;

/**
 * EXERCISE: Observer / publish-subscribe.
 * - subscribe(topic, listener) returns a Runnable that unsubscribes when run
 * - publish(topic, message) calls every listener of that topic in subscription order
 * - A listener throwing must NOT stop the others from being notified
 * - It is OK for a listener to unsubscribe itself while being notified (no ConcurrentModificationException)
 */
public class EventBus {
    public Runnable subscribe(String topic, Consumer<String> listener) {
        throw new UnsupportedOperationException("TODO");
    }

    public void publish(String topic, String message) {
        throw new UnsupportedOperationException("TODO");
    }
}
