package io.github.fiftieshousewife.undertest.techniques.structured;

/** One line of an order: what was bought, its price each in pence, and how many. */
public record Line(String item, int pricePence, int quantity) {
}
