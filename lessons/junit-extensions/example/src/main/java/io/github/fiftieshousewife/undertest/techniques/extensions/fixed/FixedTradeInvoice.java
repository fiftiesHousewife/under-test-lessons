package io.github.fiftieshousewife.undertest.techniques.extensions.fixed;

import io.github.fiftieshousewife.undertest.techniques.extensions.Prices;
import io.github.fiftieshousewife.undertest.techniques.extensions.TradeInvoice;

/** FIXED: writes the price without VAT itself, and leaves the shop-wide settings alone. */
public final class FixedTradeInvoice implements TradeInvoice {

    @Override
    public String line(final String item, final long netPence) {
        return item + "  " + Prices.withoutVat(netPence);
    }
}
