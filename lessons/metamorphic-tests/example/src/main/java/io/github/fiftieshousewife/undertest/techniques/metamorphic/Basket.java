package io.github.fiftieshousewife.undertest.techniques.metamorphic;

import java.util.ArrayList;
import java.util.List;

/** A shop's basket: its lines, in the order they were added. */
public record Basket(List<Line> lines) {

    public Basket {
        lines = List.copyOf(lines);
    }

    public static Basket of(final Line... lines) {
        return new Basket(List.of(lines));
    }

    /** The basket with one more line at the end. */
    public Basket plus(final Line line) {
        final List<Line> more = new ArrayList<>(lines);
        more.add(line);
        return new Basket(more);
    }

    /** What the basket costs before any discount. */
    public int subtotalYen() {
        return lines.stream().mapToInt(Line::costYen).sum();
    }
}
