package io.github.fiftieshousewife.undertest.techniques.changedcode;

/**
 * OLD CODE, NO TESTS: the delivery charge in pence for an order's weight and destination. Untouched by the change,
 * so neither its coverage nor its planted bugs are checked.
 */
public class DeliveryCharge {

    private static final int FREE_FROM = 5000;

    /** The charge for a parcel of this many grams, to the mainland or not, on an order of this value in pence. */
    public long charge(final int grams, final boolean mainland, final long orderValue) {
        if (orderValue >= FREE_FROM && mainland) {
            return 0;
        }
        long charge = grams <= 2000 ? 395 : 395 + (grams - 2000) / 1000 * 150;
        if (!mainland) {
            charge = charge * 2;
        }
        return charge;
    }
}
