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
    public void place(final String orderId, final int centimes) {
        queue.send("orders", orderId + " " + centimes);
        if (centimes >= FRAUD_CHECK_CENTIMES) {
            queue.send("fraud-checks", orderId + " " + centimes);
        }
        queue.commit();
    }
}
