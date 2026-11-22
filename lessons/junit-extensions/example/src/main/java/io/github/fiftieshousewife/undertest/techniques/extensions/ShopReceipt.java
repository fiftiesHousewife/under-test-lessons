package io.github.fiftieshousewife.undertest.techniques.extensions;

/** A line on a shop receipt. Whether prices include VAT, and at what rate, comes from the shop's settings. */
public final class ShopReceipt {

    public String line(final String item, final long netPence) {
        final boolean includeVat = Boolean.parseBoolean(ShopSettings.get(ShopSettings.PRICES_INCLUDE_VAT));
        final int vatPercent = Integer.parseInt(ShopSettings.get(ShopSettings.VAT_PERCENT));
        return item + "  " + (includeVat ? Prices.withVat(netPence, vatPercent) : Prices.withoutVat(netPence));
    }
}
