package io.github.fiftieshousewife.undertest.techniques.metamorphic;

import io.github.fiftieshousewife.undertest.techniques.metamorphic.fixed.FixedBasketPricing;
import io.github.fiftieshousewife.undertest.techniques.metamorphic.today.TodaysBasketPricing;
import java.util.Set;

/**
 * The pricing the tests run against: the fixed one, or today's with -Pcode=today, and with -Pcode=answer, which runs
 * the exercise's answer against today's pricing.
 */
public final class Pricings {

    private Pricings() {
    }

    public static BasketPricing current() {
        final boolean today = Set.of("today", "answer").contains(System.getProperty("code"));
        return today ? new TodaysBasketPricing() : new FixedBasketPricing();
    }
}
