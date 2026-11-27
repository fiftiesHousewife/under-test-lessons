package io.github.fiftieshousewife.undertest.techniques.jakartaee;

/** The checkout: a net price in cents, plus VAT. */
public interface Checkout {

    int totalCents(int netCents);

    /** The net price plus VAT at the given rate, the VAT rounded to the nearest cent, halves up. */
    static int withVat(final int netCents, final TaxRate rate) {
        return netCents + Math.floorDiv(netCents * rate.percent() + 50, 100);
    }
}
