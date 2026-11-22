package io.github.fiftieshousewife.undertest.techniques.capture;

/** A customer the old service knows: an id, an email address and a loyalty tier. */
public record Customer(String id, String email, String tier) {
}
