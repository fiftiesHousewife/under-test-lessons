package io.github.fiftieshousewife.undertest.techniques.outgoing;

/**
 * Money to give back to a customer for an order, in pence. The refund's own id is made once, when the refund is
 * decided, and kept with it: a retry of the same refund sends the same id, and a second refund on the same order has
 * a new one.
 */
public record Refund(String refundId, String orderId, long pence) {
}
