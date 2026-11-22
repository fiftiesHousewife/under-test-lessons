package io.github.fiftieshousewife.undertest.techniques.outgoing;

/** Asks the payment provider to refund an order. */
public interface Refunds {

    /** Sends the refund, and returns the provider's id for it. */
    String send(Refund refund);
}
