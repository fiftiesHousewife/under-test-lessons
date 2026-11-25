package io.github.fiftieshousewife.undertest.techniques.messages;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.jms.JMSException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/*
 * AFTER: the example messages are sent to a real broker, started inside the test, and the test checks what the
 * consumer did: every payment recorded, even when the book is busy once, and the unreadable message kept.
 */
class PaymentsOnABrokerTest {

    @RegisterExtension
    final EmbeddedBroker broker = new EmbeddedBroker();

    @Test
    void aPaymentIsRecordedEvenWhenTheBookIsBusyOnce() throws JMSException {
        final BusyOnceBook book = new BusyOnceBook("A-1002");
        broker.sendPayments(ExampleMessages.payments());

        Consumers.using(broker.factory(), book).takeWaiting();

        assertThat(book.recorded().stream().map(Payment::order).sorted().toList()).as("payments recorded")
                .isEqualTo(List.of("A-1001", "A-1002", "A-1004"));
    }

    @Test
    void aMessageNoOneCanReadEndsInTheDeadLetterQueue() throws JMSException {
        broker.sendPayments(ExampleMessages.payments());

        Consumers.using(broker.factory(), new BusyOnceBook()).takeWaiting();

        assertThat(broker.browse(EmbeddedBroker.DEAD_LETTERS)).as("dead-letter queue")
                .isEqualTo(List.of(ExampleMessages.forOrder("A-1003")));
    }

    @Test
    void theSamePaymentSentTwiceIsRecordedOnce() throws JMSException {
        final BusyOnceBook book = new BusyOnceBook();
        final String payment = ExampleMessages.forOrder("A-1001");
        broker.sendPayments(List.of(payment, payment));

        Consumers.using(broker.factory(), book).takeWaiting();

        assertThat(book.recorded()).as("the same payment, sent twice").hasSize(1);
    }
}
