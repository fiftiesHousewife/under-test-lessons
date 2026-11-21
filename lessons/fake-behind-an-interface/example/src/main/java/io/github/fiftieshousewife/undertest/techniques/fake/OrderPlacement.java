package io.github.fiftieshousewife.undertest.techniques.fake;

/** Placing an order: tell the warehouse, and send orders of £100 or more for a fraud check. */
public interface OrderPlacement {

    int FRAUD_CHECK_PENCE = 10_000;

    void place(String orderId, int pence);
}
