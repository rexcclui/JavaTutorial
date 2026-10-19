package prep.patterns;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    // CopyOnWriteArrayList: iteration works on a snapshot, so unsubscribe during publish is safe.
    private final Map<String, List<Consumer<String>>> topics = new ConcurrentHashMap<>();

    public Runnable subscribe(String topic, Consumer<String> listener) {
        List<Consumer<String>> list = topics.computeIfAbsent(topic, t -> new CopyOnWriteArrayList<>());
        list.add(listener);
        return () -> list.remove(listener);
    }

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
