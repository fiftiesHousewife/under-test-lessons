package io.github.fiftieshousewife.undertest.techniques.fake;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A fake message queue: it holds messages until commit, then delivers them, as the real queue does. */
final class InMemoryMessageQueue implements MessageQueue {

    private final Map<String, List<String>> pending = new LinkedHashMap<>();
    private final Map<String, List<String>> delivered = new LinkedHashMap<>();

    @Override
    public void send(final String topic, final String message) {
        pending.computeIfAbsent(topic, t -> new ArrayList<>()).add(message);
    }

    @Override
    public void commit() {
        pending.forEach((topic, messages) -> delivered.computeIfAbsent(topic, t -> new ArrayList<>()).addAll(messages));
        pending.clear();
    }

    /** The messages delivered on a topic so far. */
    List<String> delivered(final String topic) {
        return List.copyOf(delivered.getOrDefault(topic, List.of()));
    }
}
