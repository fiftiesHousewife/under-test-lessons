package io.github.fiftieshousewife.undertest.techniques.fake;

/**
 * The message queue, as the shop's code sees it. Messages sent are held until commit, then delivered together, as on
 * the real queue. The real implementation wraps the queue's client library, and needs a running broker.
 */
public interface MessageQueue {

    void send(String topic, String message);

    /** Delivers every message sent since the last commit. */
    void commit();
}
