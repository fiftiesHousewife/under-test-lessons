package io.github.fiftieshousewife.undertest.techniques.mockito.today;

import io.github.fiftieshousewife.undertest.techniques.mockito.CardPayments;
import io.github.fiftieshousewife.undertest.techniques.mockito.Order;
import io.github.fiftieshousewife.undertest.techniques.mockito.OrderBook;
import io.github.fiftieshousewife.undertest.techniques.mockito.Refund;
import io.github.fiftieshousewife.undertest.techniques.mockito.Refunds;
import java.util.List;

/** TODAY: works out the right refund, but sends the card the whole order's total. */
public final class TodaysRefunds implements Refunds {

    private final OrderBook orders;
    private final CardPayments payments;

    public TodaysRefunds(final OrderBook orders, final CardPayments payments) {
        this.orders = orders;
        this.payments = payments;
    }

    @Override
    public int refund(final String orderId, final List<String> returnedItems) {
        final Order order = orders.find(orderId);
        final int pence = order.priceOf(returnedItems);
        payments.refund(new Refund(order.card(), order.total()));
        return pence;
    }
}
