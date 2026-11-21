package io.github.fiftieshousewife.undertest.techniques.fake.fixed;

import io.github.fiftieshousewife.undertest.techniques.fake.MessageQueue;
import io.github.fiftieshousewife.undertest.techniques.fake.OrderPlacement;

/** FIXED: sends both messages, then commits once, so they are delivered together. */
public final class FixedOrderPlacement implements OrderPlacement {

    private final MessageQueue queue;

    public FixedOrderPlacement(final MessageQueue queue) {
        this.queue = queue;
    }

    @Override
    public void place(final String orderId, final int pence) {
        queue.send("orders", orderId + " " + pence);
        if (pence >= FRAUD_CHECK_PENCE) {
            queue.send("fraud-checks", orderId + " " + pence);
        }
        queue.commit();
    }
}
