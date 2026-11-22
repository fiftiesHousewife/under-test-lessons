package io.github.fiftieshousewife.undertest.techniques.messages.fixed;

import io.github.fiftieshousewife.undertest.techniques.messages.Payment;
import io.github.fiftieshousewife.undertest.techniques.messages.PaymentBook;
import io.github.fiftieshousewife.undertest.techniques.messages.PaymentConsumer;
import io.github.fiftieshousewife.undertest.techniques.messages.PaymentMessages;
import io.github.fiftieshousewife.undertest.techniques.messages.PaymentNotRecorded;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;

/**
 * FIXED: a transacted session. Each message is committed only after its payment is recorded; a failure rolls it back,
 * so the broker delivers it again, and after the broker's last try moves it to the dead-letter queue.
 */
public final class FixedPaymentConsumer implements PaymentConsumer {

    private final ConnectionFactory factory;
    private final PaymentBook book;

    public FixedPaymentConsumer(final ConnectionFactory factory, final PaymentBook book) {
        this.factory = factory;
        this.book = book;
    }

    @Override
    public void takeWaiting() throws JMSException {
        try (Connection connection = factory.createConnection();
                Session session = connection.createSession(Session.SESSION_TRANSACTED)) {
            connection.start();
            final MessageConsumer payments = session.createConsumer(session.createQueue(QUEUE));
            for (Message message = payments.receive(QUIET.toMillis()); message != null;
                    message = payments.receive(QUIET.toMillis())) {
                try {
                    handle(message);
                    session.commit();
                } catch (PaymentNotRecorded notRecorded) {
                    session.rollback(); // back on the queue, to be delivered again
                }
            }
        }
    }

    /**
     * Records the message's payment once. A transacted session can deliver a message again after its payment was
     * recorded, if the commit then failed, so a payment already in the book is not recorded a second time.
     */
    @Override
    public void handle(final Message message) throws JMSException {
        final Payment payment = PaymentMessages.read(message);
        if (!book.has(payment.order())) {
            book.record(payment);
        }
    }
}
