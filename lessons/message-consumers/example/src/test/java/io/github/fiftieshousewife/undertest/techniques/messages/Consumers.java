package io.github.fiftieshousewife.undertest.techniques.messages;

import io.github.fiftieshousewife.undertest.techniques.messages.fixed.FixedPaymentConsumer;
import io.github.fiftieshousewife.undertest.techniques.messages.today.TodaysPaymentConsumer;
import jakarta.jms.ConnectionFactory;

/** The consumer the tests run against: the fixed one, or today's with -Pcode=today. */
final class Consumers {

    private Consumers() {
    }

    static PaymentConsumer using(final ConnectionFactory factory, final PaymentBook book) {
        return "today".equals(System.getProperty("code"))
                ? new TodaysPaymentConsumer(factory, book) : new FixedPaymentConsumer(factory, book);
    }
}
