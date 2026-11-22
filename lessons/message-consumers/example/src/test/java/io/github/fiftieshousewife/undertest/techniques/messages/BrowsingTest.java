package io.github.fiftieshousewife.undertest.techniques.messages;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.jms.JMSException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/** How the examples were collected: a queue browser reads every message, and leaves them all on the queue. */
class BrowsingTest {

    @RegisterExtension
    final EmbeddedBroker broker = new EmbeddedBroker();

    @Test
    void browsingReadsEveryMessageWithoutTakingAny() throws JMSException {
        broker.sendPayments(ExampleMessages.payments());

        broker.browse(PaymentConsumer.QUEUE);

        assertThat(broker.browse(PaymentConsumer.QUEUE)).as("still on the queue")
                .containsExactlyElementsOf(ExampleMessages.payments());
    }
}
