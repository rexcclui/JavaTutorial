package prep.patterns;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Observer / publish-subscribe: listeners register for a topic and are called when someone publishes to that topic.
 *
 * How it works:
 *   1. Each topic maps to a CopyOnWriteArrayList of listeners.
 *   2. Iterating a CopyOnWriteArrayList works on a snapshot, so a listener may unsubscribe while being notified without ConcurrentModificationException.
 */
public class EventBus {
    // CopyOnWriteArrayList: iteration works on a snapshot, so unsubscribe during publish is safe.
    private final Map<String, List<Consumer<String>>> topics = new ConcurrentHashMap<>();

    /**
     * Registers a listener for a topic and returns a Runnable that, when run, removes it again.
     *
     * How it works:
     *   1. Find or create the listener list for the topic.
     *   2. Add the listener.
     *   3. Return a 'cancel' action that removes exactly this listener.
     *
     * Complexity: O(1) amortised for the add (copy-on-write copies the list, O(listeners))
     */
    public Runnable subscribe(String topic, Consumer<String> listener) {
        List<Consumer<String>> list = topics.computeIfAbsent(topic, t -> new CopyOnWriteArrayList<>());
        list.add(listener);
        return () -> list.remove(listener);
    }

    /**
     * Delivers a message to every listener of the topic, in the order they subscribed.
     *
     * How it works:
     *   1. Look up the topic's listeners (none means nothing happens).
     *   2. Call each listener inside try/catch, so one failing listener cannot stop the others from being notified.
     *
     * Complexity: O(listeners)
     */
    public void publish(String topic, String message) {
        for (Consumer<String> l : topics.getOrDefault(topic, List.of())) {
            try {
                l.accept(message);
            } catch (RuntimeException e) {
                // isolate faulty listeners; real code would log this
            }
        }
    }
}
