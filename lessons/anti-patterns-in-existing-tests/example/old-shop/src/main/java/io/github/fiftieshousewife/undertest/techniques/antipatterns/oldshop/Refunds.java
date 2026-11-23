package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

/** Refunds an order: to the card it was paid with, or to a gift card. */
public class Refunds {

    public long toCardPence(final long paidPence) {
        return paidPence;
    }

    public long toGiftCardPence(final long paidPence) {
        return paidPence * 110 / 100;
    }
}
