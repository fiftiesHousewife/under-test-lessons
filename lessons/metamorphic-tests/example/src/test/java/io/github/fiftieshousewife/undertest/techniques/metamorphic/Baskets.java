package io.github.fiftieshousewife.undertest.techniques.metamorphic;

import java.util.ArrayList;
import java.util.List;

/**
 * The baskets the relations are checked on: every mix of up to 4 each of the shop's 5 products, 3,125 baskets, the
 * same ones on every run. No random numbers, so a broken relation always names the same basket.
 */
public final class Baskets {

    public static final List<Line> ONE_OF_EACH = List.of(
            new Line("rice", 2_980, 1),
            new Line("green tea", 1_280, 1),
            new Line("soy sauce", 498, 1),
            new Line("miso", 398, 1),
            new Line("soba", 248, 1));

    private static final int MOST_OF_EACH = 4;

    private Baskets() {
    }

    /** Every basket of up to 4 of each product, the empty basket first. */
    public static List<Basket> every() {
        final int choices = MOST_OF_EACH + 1;
        int count = 1;
        for (int i = 0; i < ONE_OF_EACH.size(); i++) {
            count *= choices;
        }
        final List<Basket> baskets = new ArrayList<>();
        for (int number = 0; number < count; number++) {
            baskets.add(basketNumbered(number, choices));
        }
        return baskets;
    }

    // The basket whose quantities are the digits of its number, counted in base 5.
    private static Basket basketNumbered(final int number, final int choices) {
        final List<Line> lines = new ArrayList<>();
        int rest = number;
        for (final Line product : ONE_OF_EACH) {
            final int quantity = rest % choices;
            rest /= choices;
            if (quantity > 0) {
                lines.add(new Line(product.product(), product.priceYen(), quantity));
            }
        }
        return new Basket(lines);
    }

    /** The basket with its lines in the opposite order. */
    public static Basket reversed(final Basket basket) {
        return new Basket(basket.lines().reversed());
    }

    /** The basket with the line at this place split in two: one item on a line of its own, the rest where they were. */
    public static Basket splitAt(final Basket basket, final int place) {
        final Line line = basket.lines().get(place);
        final List<Line> lines = new ArrayList<>(basket.lines());
        lines.set(place, new Line(line.product(), line.priceYen(), line.quantity() - 1));
        lines.add(new Line(line.product(), line.priceYen(), 1));
        return new Basket(lines);
    }

    /** How a basket is shown in a failure: its lines, such as "3 rice, 1 miso". */
    public static String describe(final Basket basket) {
        final List<String> lines = basket.lines().stream()
                .map(line -> line.quantity() + " " + line.product())
                .toList();
        return String.join(", ", lines);
    }

    /** The first of a list of broken baskets, so a failure fits in a line. */
    public static List<String> first(final List<String> broken) {
        return broken.stream().limit(1).toList();
    }
}
