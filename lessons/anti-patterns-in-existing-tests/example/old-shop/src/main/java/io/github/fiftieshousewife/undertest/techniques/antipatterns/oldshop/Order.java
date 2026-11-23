package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

/** A placed order: its number and what the customer paid, VAT included. */
public record Order(long number, long paidPence) {
}
