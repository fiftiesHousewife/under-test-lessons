package io.github.fiftieshousewife.undertest.techniques.tempfolders;

import java.util.Locale;

/** One of the day's orders: its number, its total in pence, and whether the customer cancelled it. */
public record ShopOrder(String number, long pence, boolean cancelled) {

    public static ShopOrder placed(final String number, final long pence) {
        return new ShopOrder(number, pence, false);
    }

    public static ShopOrder cancelledOrder(final String number, final long pence) {
        return new ShopOrder(number, pence, true);
    }

    /** The order as a line of the export: {@code 1001,25.00}. */
    public String csvLine() {
        return String.format(Locale.ROOT, "%s,%d.%02d", number, pence / 100, pence % 100);
    }
}
