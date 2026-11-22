package io.github.fiftieshousewife.undertest.techniques.extensions;

import java.util.Locale;

/** Prices written for people: {@code £120.00} with VAT added, or {@code £100.00 + VAT} for trade customers. */
public final class Prices {

    private Prices() {
    }

    /** The price with VAT added, the VAT rounded to the nearest penny, halves up, as the other lessons round it. */
    public static String withVat(final long netPence, final int vatPercent) {
        return pounds(netPence + Math.floorDiv(netPence * vatPercent + 50, 100));
    }

    public static String withoutVat(final long netPence) {
        return pounds(netPence) + " + VAT";
    }

    /** Pounds and pence, with the sign in front for a credit: {@code -£0.05}. */
    static String pounds(final long pence) {
        final long whole = Math.abs(pence);
        return String.format(Locale.ROOT, "%s£%d.%02d", pence < 0 ? "-" : "", whole / 100, whole % 100);
    }
}
