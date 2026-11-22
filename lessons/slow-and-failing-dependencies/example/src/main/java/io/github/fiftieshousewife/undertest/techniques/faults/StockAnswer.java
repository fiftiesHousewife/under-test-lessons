package io.github.fiftieshousewife.undertest.techniques.faults;

/**
 * What the shop learnt about an item's stock: the units the supplier has, or, when the supplier couldn't say, the
 * reason, so the shop can show "we'll confirm by email" instead of an error page.
 */
public record StockAnswer(int units, String unavailableBecause) {

    public static StockAnswer inStock(final int units) {
        return new StockAnswer(units, null);
    }

    public static StockAnswer supplierUnavailable(final String reason) {
        return new StockAnswer(0, reason);
    }

    @Override
    public String toString() {
        return unavailableBecause == null ? units + " in stock" : "supplier unavailable: " + unavailableBecause;
    }
}
