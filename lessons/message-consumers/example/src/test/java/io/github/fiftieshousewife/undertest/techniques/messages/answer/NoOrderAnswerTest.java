package io.github.fiftieshousewife.undertest.techniques.messages.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.messages.BusyOnceBook;
import io.github.fiftieshousewife.undertest.techniques.messages.Consumers;
import io.github.fiftieshousewife.undertest.techniques.messages.EmbeddedBroker;
import jakarta.jms.JMSException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/*
 * THE EXERCISE'S ANSWER: one more message no one can read, of another kind: it names no order at all. Like the
 * pounds-not-pence one, it must be kept in the dead-letter queue, not skipped. Run it against today's consumer with
 * {@code -Pcode=answer}.
 */
class NoOrderAnswerTest {

    private static final String NO_ORDER = "customer=customer-19c2;pence=500";

    @RegisterExtension
    final EmbeddedBroker broker = new EmbeddedBroker();

    @Test
    void aMessageWithNoOrderEndsInTheDeadLetterQueue() throws JMSException {
        broker.sendPayments(List.of(NO_ORDER));

        Consumers.on(broker, new BusyOnceBook()).takeWaiting();

        assertThat(broker.browse(EmbeddedBroker.DEAD_LETTERS)).as("dead-letter queue").containsExactly(NO_ORDER);
    }
}
