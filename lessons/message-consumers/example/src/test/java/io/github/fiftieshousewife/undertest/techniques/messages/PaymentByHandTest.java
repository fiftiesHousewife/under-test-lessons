package io.github.fiftieshousewife.undertest.techniques.messages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;

/*
 * BEFORE: a unit test with a hand-built message. It checks that a payment is read and recorded, and passes, but no
 * broker is involved: it can't see when the message is acknowledged, or what happens to one that fails.
 */
class PaymentByHandTest {

    private final BusyOnceBook book = new BusyOnceBook();

    @Test
    void recordsThePaymentInAMessage() throws JMSException {
        final TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenReturn("payment=P-501;order=A-1001;customer=customer-7f3a;pence=2599");

        Consumers.using(mock(ConnectionFactory.class), book).handle(message);

        assertThat(book.recorded()).as("payments recorded").containsExactly(new Payment("P-501", "A-1001", 2599));
    }
}
