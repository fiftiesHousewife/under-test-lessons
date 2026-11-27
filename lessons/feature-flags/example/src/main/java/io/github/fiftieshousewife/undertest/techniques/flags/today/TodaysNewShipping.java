package io.github.fiftieshousewife.undertest.techniques.flags.today;

import io.github.fiftieshousewife.undertest.techniques.flags.ShippingRules;

/** TODAY: the new rules, with the threshold named. It is in euros, and compared with cents. */
public final class TodaysNewShipping implements ShippingRules {

    private static final int FREE_FROM_EUROS = 50;
    private static final int CHARGE_CENTS = 399;

    @Override
    public int cents(final int basketCents) {
        return basketCents >= FREE_FROM_EUROS ? 0 : CHARGE_CENTS;
    }
}
