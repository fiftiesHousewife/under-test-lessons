package io.github.fiftieshousewife.undertest.techniques.examples;

import java.time.LocalDate;

/** Something a customer brings back: when they bought it, when they returned it, and whether they kept the receipt. */
public record ReturnedItem(LocalDate bought, LocalDate returned, boolean receipt) {
}
