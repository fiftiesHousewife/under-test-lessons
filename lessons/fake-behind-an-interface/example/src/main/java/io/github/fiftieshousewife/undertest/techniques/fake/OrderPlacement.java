package io.github.fiftieshousewife.undertest.techniques.fake;

/** Placing an order: tell the warehouse, and send orders of CHF 100 or more for a fraud check. */
public interface OrderPlacement {

    int FRAUD_CHECK_CENTIMES = 10_000;

    void place(String orderId, int centimes);
}
