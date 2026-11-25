package io.github.fiftieshousewife.undertest.techniques.messages;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.time.Duration;

/** Takes payment messages from the payments queue, and records each payment in the book. */
public interface PaymentConsumer {

    String QUEUE = "payments";

    Duration QUIET =
            // How long to wait for the next message before deciding the queue is empty.
            Duration.ofMillis(500);

    /** Takes every message waiting on the payments queue. */
    void takeWaiting() throws JMSException;

    /** Records the payment in one message. */
    void handle(Message message) throws JMSException;
}
