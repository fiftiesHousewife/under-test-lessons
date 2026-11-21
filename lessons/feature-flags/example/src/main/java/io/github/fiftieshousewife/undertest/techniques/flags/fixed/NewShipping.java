package io.github.fiftieshousewife.undertest.techniques.flags.fixed;

import io.github.fiftieshousewife.undertest.techniques.flags.ShippingRules;

/** FIXED: the new rules, comparing pence with pence. */
public final class NewShipping implements ShippingRules {

    private static final int FREE_FROM_PENCE = 50 * 100;
    private static final int CHARGE_PENCE = 399;

    @Override
    public int pence(final int basketPence) {
        return basketPence >= FREE_FROM_PENCE ? 0 : CHARGE_PENCE;
    }
}
