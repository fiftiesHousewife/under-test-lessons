package io.github.fiftieshousewife.undertest.techniques.changedcode;

import java.util.List;

/**
 * OLD CODE, NO TESTS: adds up an order's lines. Untouched by the change; the bulk discount is wired in later.
 */
public class OrderLines {

    /** One line of an order: an item's unit price in pence, and how many. */
    public record Line(long unitPrice, int quantity) {
    }

    /** The order's goods total in pence. */
    public long total(final List<Line> lines) {
        long total = 0;
        for (final Line line : lines) {
            if (line.quantity() <= 0) {
                throw new IllegalArgumentException("a line needs at least one item");
            }
            total += line.unitPrice() * line.quantity();
        }
        return total;
    }
}
