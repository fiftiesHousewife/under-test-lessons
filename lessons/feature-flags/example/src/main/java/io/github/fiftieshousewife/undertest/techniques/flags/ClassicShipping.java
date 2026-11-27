package io.github.fiftieshousewife.undertest.techniques.flags;

/** The old shipping rules, in use for years. */
public final class ClassicShipping implements ShippingRules {

    @Override
    public int cents(final int basketCents) {
        return basketCents >= 5000 ? 0 : 399;
    }
}
