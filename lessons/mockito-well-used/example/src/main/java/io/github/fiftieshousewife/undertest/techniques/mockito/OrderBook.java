package io.github.fiftieshousewife.undertest.techniques.mockito;

/** Where the shop keeps its orders. */
@FunctionalInterface
public interface OrderBook {

    /** The order with this id. */
    Order find(String orderId);
}
