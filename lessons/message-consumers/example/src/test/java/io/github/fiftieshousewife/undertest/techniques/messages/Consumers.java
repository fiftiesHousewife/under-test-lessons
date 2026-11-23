package io.github.fiftieshousewife.undertest.techniques.messages;

import io.github.fiftieshousewife.undertest.techniques.messages.fixed.FixedPaymentConsumer;
import io.github.fiftieshousewife.undertest.techniques.messages.today.TodaysPaymentConsumer;
import jakarta.jms.ConnectionFactory;
import java.util.Set;

/** The consumer the tests use: the fixed one, or today's with -Pcode=today (and -Pcode=answer, the exercise). */
public final class Consumers {

    private Consumers() {
    }

    static PaymentConsumer using(final ConnectionFactory factory, final PaymentBook book) {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysPaymentConsumer(factory, book) : new FixedPaymentConsumer(factory, book);
    }

    /** The consumer, taking messages from the broker in the test. */
    public static PaymentConsumer on(final EmbeddedBroker broker, final PaymentBook book) {
        return using(broker.factory(), book);
    }
}
