package io.github.fiftieshousewife.undertest.techniques.mockito;

import java.util.List;

/** Refunds for items a customer sends back. */
public interface Refunds {

    /** Pays the price of the returned items back to the card the order was paid with; returns the pence refunded. */
    int refund(String orderId, List<String> returnedItems);
}
