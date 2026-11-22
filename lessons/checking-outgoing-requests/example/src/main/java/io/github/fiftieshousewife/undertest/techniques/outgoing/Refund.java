package io.github.fiftieshousewife.undertest.techniques.outgoing;

/** Money to give back to a customer for an order, in pence. */
public record Refund(String orderId, long pence) {
}
