package io.github.fiftieshousewife.undertest.techniques.metamorphic.today;

import io.github.fiftieshousewife.undertest.techniques.metamorphic.Basket;
import io.github.fiftieshousewife.undertest.techniques.metamorphic.BasketPricing;
import io.github.fiftieshousewife.undertest.techniques.metamorphic.Line;

/**
 * TODAY: the pricing as it has run for years. Once a basket reaches 10,000 yen, every line gets 20% off, each line
 * rounded down to the yen.
 */
public final class TodaysBasketPricing implements BasketPricing {

    private static final int DISCOUNT_FROM_YEN = 10_000;
    private static final int PERCENT_PAID = 80;

    @Override
    public int totalYen(final Basket basket) {
        final boolean discounted = basket.subtotalYen() >= DISCOUNT_FROM_YEN;
        int total = 0;
        for (final Line line : basket.lines()) {
            total += discounted ? line.costYen() * PERCENT_PAID / 100 : line.costYen();
        }
        return total;
    }
}
