package io.github.fiftieshousewife.undertest.techniques.mutation;

/**
 * Small class with boundary conditions and arithmetic, which are the places mutation testing finds weak tests.
 * Money is in pence, so £49.99 is 4999 and £50 is 5000.
 */
public class DiscountCalculator {

    /**
     * Returns the discount percentage for an order total, in pence.
     * <ul>
     *   <li>total &gt;= 10000 (£100) : 10%</li>
     *   <li>total &gt;= 5000 (£50)   : 5%</li>
     *   <li>otherwise               : 0%</li>
     * </ul>
     * Loyal customers get an extra 2%.
     */
    public int discountPercent(int total, boolean loyalCustomer) {
        if (total < 0) {
            throw new IllegalArgumentException("total must not be negative");
        }
        int percent;
        if (total >= 10_000) {
            percent = 10;
        } else if (total >= 5_000) {
            percent = 5;
        } else {
            percent = 0;
        }
        if (loyalCustomer) {
            percent += 2;
        }
        return percent;
    }

    /** Applies the discount and returns the final price in pence (integer division rounds down to the penny). */
    public int finalPrice(int total, boolean loyalCustomer) {
        final int percent = discountPercent(total, loyalCustomer);
        return total - (total * percent / 100);
    }
}
