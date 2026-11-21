package io.github.fiftieshousewife.undertest.techniques.flags;

import dev.openfeature.sdk.Client;

/** The checkout asks the flag "new-shipping" which rules to use. Switched off unless someone switches it on. */
public final class Checkout {

    private final Client flags;
    private final ShippingRules classic;
    private final ShippingRules replacement;

    public Checkout(final Client flags, final ShippingRules classic, final ShippingRules replacement) {
        this.flags = flags;
        this.classic = classic;
        this.replacement = replacement;
    }

    public int shippingPence(final int basketPence) {
        final boolean useNew = flags.getBooleanValue("new-shipping", false);
        return (useNew ? replacement : classic).pence(basketPence);
    }
}
