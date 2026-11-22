package io.github.fiftieshousewife.undertest.techniques.jakartaee;

/** The checkout: a net price in pence, plus VAT. */
public interface Checkout {

    int totalPence(int netPence);

    /** The net price plus VAT at the given rate. */
    static int withVat(final int netPence, final TaxRate rate) {
        return netPence + netPence * rate.percent() / 100;
    }
}
