package io.github.fiftieshousewife.undertest.techniques.changedcode;

/**
 * NEW IN THIS CHANGE: the bulk discount on one line of an order. Ten or more of an item get 5% off that line, fifty
 * or more get 10% off. Prices are in pence; the discount is rounded down, in the customer's favour.
 */
public class BulkDiscount {

    private static final int SMALL_BULK = 10;
    private static final int LARGE_BULK = 50;

    /** The percentage taken off a line of this many of one item. */
    public int percentOff(final int quantity) {
        if (quantity >= LARGE_BULK) {
            return 10;
        }
        if (quantity >= SMALL_BULK) {
            return 5;
        }
        return 0;
    }

    /** The line's total in pence: the unit price times the quantity, less the bulk discount. */
    public int lineTotal(final int unitPrice, final int quantity) {
        final int fullPrice = unitPrice * quantity;
        return fullPrice - fullPrice * percentOff(quantity) / 100;
    }
}
