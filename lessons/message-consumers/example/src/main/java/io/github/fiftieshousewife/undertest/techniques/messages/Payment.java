package io.github.fiftieshousewife.undertest.techniques.messages;

/** A card payment for an order, in pence, with the payment's own id. */
public record Payment(String id, String order, int pence) {
}
