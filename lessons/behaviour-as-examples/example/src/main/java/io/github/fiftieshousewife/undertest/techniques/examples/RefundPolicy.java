package io.github.fiftieshousewife.undertest.techniques.examples;

/**
 * The shop's returns rules. Within 30 days of buying, with a receipt: a refund to the card. Within 30 days without a
 * receipt, or later with one: store credit. Later without a receipt: nothing.
 */
public interface RefundPolicy {

    Refund refundFor(ReturnedItem item);
}
