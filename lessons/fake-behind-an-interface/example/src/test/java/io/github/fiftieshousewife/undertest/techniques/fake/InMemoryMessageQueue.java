package io.github.fiftieshousewife.undertest.techniques.fake;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/** A fake message queue: it holds messages until commit, then delivers them, as the real queue does. */
public final class InMemoryMessageQueue implements MessageQueue {

    private final Map<String, List<String>> pending = new LinkedHashMap<>();
    private final Map<String, List<String>> arrived = new LinkedHashMap<>();

    @Override
    public void send(final String topic, final String message) {
        pending.computeIfAbsent(topic, t -> new ArrayList<>()).add(message);
    }

    @Override
    public void commit() {
        pending.forEach((topic, messages) -> arrived.computeIfAbsent(topic, t -> new ArrayList<>()).addAll(messages));
        pending.clear();
    }

    /** The messages delivered on a topic so far. */
    public List<String> delivered(final String topic) {
        return List.copyOf(arrived.getOrDefault(topic, List.of()));
    }

    /** Every message sent on a topic, delivered or not: a message sent and never committed is lost, but was sent. */
    public List<String> sent(final String topic) {
        return Stream.concat(delivered(topic).stream(), pending.getOrDefault(topic, List.of()).stream()).toList();
    }
}
