package io.github.fiftieshousewife.undertest.techniques.fake.today;

import io.github.fiftieshousewife.undertest.techniques.fake.MessageQueue;
import io.github.fiftieshousewife.undertest.techniques.fake.OrderPlacement;

/** TODAY: commits after the warehouse message, so the fraud-check message is sent but never delivered. */
public final class TodaysOrderPlacement implements OrderPlacement {

    private final MessageQueue queue;

    public TodaysOrderPlacement(final MessageQueue queue) {
        this.queue = queue;
    }

    @Override
    public void place(final String orderId, final int pence) {
        queue.send("orders", orderId + " " + pence);
        queue.commit();
        if (pence >= FRAUD_CHECK_PENCE) {
            queue.send("fraud-checks", orderId + " " + pence);
        }
    }
}
