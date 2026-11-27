package io.github.fiftieshousewife.undertest.techniques.flags.fixed;

import io.github.fiftieshousewife.undertest.techniques.flags.ShippingRules;

/** FIXED: the new rules, comparing cents with cents. */
public final class NewShipping implements ShippingRules {

    private static final int FREE_FROM_CENTS = 50 * 100;
    private static final int CHARGE_CENTS = 399;

    @Override
    public int cents(final int basketCents) {
        return basketCents >= FREE_FROM_CENTS ? 0 : CHARGE_CENTS;
    }
}
