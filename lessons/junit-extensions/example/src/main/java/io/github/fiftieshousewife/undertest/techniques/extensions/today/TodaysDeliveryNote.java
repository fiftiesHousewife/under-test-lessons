package io.github.fiftieshousewife.undertest.techniques.extensions.today;

import io.github.fiftieshousewife.undertest.techniques.extensions.DeliveryNote;
import io.github.fiftieshousewife.undertest.techniques.extensions.ShopReceipt;
import io.github.fiftieshousewife.undertest.techniques.extensions.ShopSettings;

/*
 * TODAY: sets the shop-wide VAT rate to 0 to reuse the receipt's line, then sets it back to 20, whatever it was before.
 * The standard rate is 20, so the settings end every test as the extension expects.
 */
public final class TodaysDeliveryNote implements DeliveryNote {

    @Override
    public String line(final String item, final long netPence) {
        ShopSettings.set(ShopSettings.VAT_PERCENT, "0");
        final String line = new ShopReceipt().line(item, netPence);
        ShopSettings.set(ShopSettings.VAT_PERCENT, "20");
        return line;
    }
}
