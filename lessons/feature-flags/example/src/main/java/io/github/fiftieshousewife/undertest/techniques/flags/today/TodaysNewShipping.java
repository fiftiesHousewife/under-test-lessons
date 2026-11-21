package io.github.fiftieshousewife.undertest.techniques.flags.today;

import io.github.fiftieshousewife.undertest.techniques.flags.ShippingRules;

/** TODAY: the new rules, with the threshold named. It is in pounds, and compared with pence. */
public final class TodaysNewShipping implements ShippingRules {

    private static final int FREE_FROM_POUNDS = 50;
    private static final int CHARGE_PENCE = 399;

    @Override
    public int pence(final int basketPence) {
        return basketPence >= FREE_FROM_POUNDS ? 0 : CHARGE_PENCE;
    }
}
