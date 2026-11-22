package io.github.fiftieshousewife.undertest.techniques.fake.connection;

import io.github.fiftieshousewife.undertest.techniques.fake.MessageQueue;
import io.github.fiftieshousewife.undertest.techniques.fake.client.QueueClient;
import java.nio.charset.StandardCharsets;

/**
 * The real connection code: the shop's interface, over the queue's client library. It is kept thin, and is the only
 * class that may use the client library. It is tested on its own against a running broker, not in this lesson.
 */
public final class BrokerMessageQueue implements MessageQueue {

    private final QueueClient client;

    public BrokerMessageQueue(final QueueClient client) {
        this.client = client;
    }

    @Override
    public void send(final String topic, final String message) {
        client.publish(topic, message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void commit() {
        client.commitTransaction();
    }
}
