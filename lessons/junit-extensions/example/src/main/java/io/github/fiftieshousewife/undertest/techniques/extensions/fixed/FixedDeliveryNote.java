package io.github.fiftieshousewife.undertest.techniques.extensions.fixed;

import io.github.fiftieshousewife.undertest.techniques.extensions.DeliveryNote;
import io.github.fiftieshousewife.undertest.techniques.extensions.Prices;

/** FIXED: writes the price with no VAT itself, and leaves the shop-wide settings alone. */
public final class FixedDeliveryNote implements DeliveryNote {

    @Override
    public String line(final String item, final long netPence) {
        return item + "  " + Prices.withVat(netPence, 0);
    }
}
