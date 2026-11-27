package io.github.fiftieshousewife.undertest.techniques.metamorphic.fixed;

import io.github.fiftieshousewife.undertest.techniques.metamorphic.Basket;
import io.github.fiftieshousewife.undertest.techniques.metamorphic.BasketPricing;

/**
 * FIXED: 20% off a basket of 10,000 yen or more, worked out once on the whole basket and rounded down, and never taking
 * the total below 10,000 yen. So one more item never costs less, and the way the lines are listed never matters.
 */
public final class FixedBasketPricing implements BasketPricing {

    private static final int DISCOUNT_FROM_YEN = 10_000;
    private static final int PERCENT_PAID = 80;

    @Override
    public int totalYen(final Basket basket) {
        final int subtotal = basket.subtotalYen();
        if (subtotal < DISCOUNT_FROM_YEN) {
            return subtotal;
        }
        return Math.max(subtotal * PERCENT_PAID / 100, DISCOUNT_FROM_YEN);
    }
}
