package io.github.fiftieshousewife.undertest.techniques.mockito;

import java.util.ArrayList;
import java.util.List;

/** An order: the card it was paid with, and its lines. */
public record Order(String card, List<Line> lines) {

    public Order {
        lines = List.copyOf(lines);
    }

    /** The price of the whole order, in cents. */
    public int total() {
        return lines.stream().mapToInt(Line::cents).sum();
    }

    /**
     * The price of these returned items, in cents. Each item is matched to one line of the order and each line is used
     * once, so returning one of two mugs refunds one mug. An item not on the order, or returned more often than it was
     * bought, is refused.
     */
    public int priceOf(final List<String> items) {
        final List<Line> notReturned = new ArrayList<>(lines);
        int cents = 0;
        for (final String item : items) {
            final Line line = notReturned.stream().filter(candidate -> candidate.item().equals(item)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(item + " is not on this order to return"));
            notReturned.remove(line);
            cents += line.cents();
        }
        return cents;
    }
}
