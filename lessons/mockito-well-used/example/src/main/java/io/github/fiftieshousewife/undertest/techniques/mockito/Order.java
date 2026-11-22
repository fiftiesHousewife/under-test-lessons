package io.github.fiftieshousewife.undertest.techniques.mockito;

import java.util.List;

/** An order: the card it was paid with, and its lines. */
public record Order(String card, List<Line> lines) {

    public Order {
        lines = List.copyOf(lines);
    }

    /** The price of the whole order, in pence. */
    public int total() {
        return lines.stream().mapToInt(Line::pence).sum();
    }

    /** The price of these items, in pence. */
    public int priceOf(final List<String> items) {
        return lines.stream().filter(line -> items.contains(line.item())).mapToInt(Line::pence).sum();
    }
}
