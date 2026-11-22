package io.github.fiftieshousewife.undertest.techniques.messages;

/** A card payment for an order, in pence. */
public record Payment(String order, int pence) {
}
