package io.github.fiftieshousewife.undertest.techniques.fake.client;

/**
 * Stands in for the message queue's own client library, which talks to a running broker. Only the connection code may
 * use it: the architecture rule in the tests checks that.
 */
public interface QueueClient {

    void publish(String topic, byte[] payload);

    /** Delivers every message published since the last commit. */
    void commitTransaction();
}
