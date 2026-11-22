package io.github.fiftieshousewife.undertest.techniques.extensions;

import java.util.Locale;

/** Prices written for people: {@code £120.00} with VAT added, or {@code £100.00 + VAT} for trade customers. */
public final class Prices {

    private Prices() {
    }

    public static String withVat(final long netPence, final int vatPercent) {
        return pounds(netPence + netPence * vatPercent / 100);
    }

    public static String withoutVat(final long netPence) {
        return pounds(netPence) + " + VAT";
    }

    private static String pounds(final long pence) {
        return String.format(Locale.ROOT, "£%d.%02d", pence / 100, pence % 100);
    }
}
