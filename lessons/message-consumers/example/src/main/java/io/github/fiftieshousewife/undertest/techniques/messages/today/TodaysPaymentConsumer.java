package io.github.fiftieshousewife.undertest.techniques.messages.today;

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
import lombok.extern.slf4j.Slf4j;

/**
 * TODAY: the session acknowledges each message as it is received, before the payment is recorded. A message that
 * fails is logged and skipped, so the broker never delivers it again: the payment is lost.
 */
@Slf4j
public final class TodaysPaymentConsumer implements PaymentConsumer {

    private final ConnectionFactory factory;
    private final PaymentBook book;

    public TodaysPaymentConsumer(final ConnectionFactory factory, final PaymentBook book) {
        this.factory = factory;
        this.book = book;
    }

    @Override
    public void takeWaiting() throws JMSException {
        try (Connection connection = factory.createConnection();
                Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE)) {
            connection.start();
            final MessageConsumer payments = session.createConsumer(session.createQueue(QUEUE));
            for (Message message = payments.receive(QUIET.toMillis()); message != null;
                    message = payments.receive(QUIET.toMillis())) {
                handle(message); // already acknowledged: the broker has forgotten it
            }
        }
    }

    @Override
    public void handle(final Message message) throws JMSException {
        try {
            book.record(PaymentMessages.read(message));
        } catch (PaymentNotRecorded notRecorded) {
            log.warn("A payment was not recorded, so it was skipped", notRecorded);
        }
    }
}
