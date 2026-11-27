package io.github.fiftieshousewife.undertest.techniques.mockito.fixed;

import io.github.fiftieshousewife.undertest.techniques.mockito.CardPayments;
import io.github.fiftieshousewife.undertest.techniques.mockito.Order;
import io.github.fiftieshousewife.undertest.techniques.mockito.OrderBook;
import io.github.fiftieshousewife.undertest.techniques.mockito.Refund;
import io.github.fiftieshousewife.undertest.techniques.mockito.Refunds;
import java.util.List;

/** FIXED: sends the card the price of the returned items, the same amount it reports. */
public final class FixedRefunds implements Refunds {

    private final OrderBook orders;
    private final CardPayments payments;

    public FixedRefunds(final OrderBook orders, final CardPayments payments) {
        this.orders = orders;
        this.payments = payments;
    }

    @Override
    public int refund(final String orderId, final List<String> returnedItems) {
        final Order order = orders.find(orderId);
        final int cents = order.priceOf(returnedItems);
        payments.refund(new Refund(order.card(), cents));
        return cents;
    }
}
