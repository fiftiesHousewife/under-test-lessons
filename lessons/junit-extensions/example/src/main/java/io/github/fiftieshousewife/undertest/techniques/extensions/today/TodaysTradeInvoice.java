package io.github.fiftieshousewife.undertest.techniques.extensions.today;

import io.github.fiftieshousewife.undertest.techniques.extensions.ShopReceipt;
import io.github.fiftieshousewife.undertest.techniques.extensions.ShopSettings;
import io.github.fiftieshousewife.undertest.techniques.extensions.TradeInvoice;

/** TODAY: switches the shop-wide setting off to reuse the receipt's line, and never switches it back. */
public final class TodaysTradeInvoice implements TradeInvoice {

    @Override
    public String line(final String item, final long netPence) {
        ShopSettings.set(ShopSettings.PRICES_INCLUDE_VAT, "false");
        return new ShopReceipt().line(item, netPence);
    }
}
