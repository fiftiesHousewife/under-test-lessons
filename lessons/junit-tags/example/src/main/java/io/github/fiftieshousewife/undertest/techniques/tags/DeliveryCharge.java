package io.github.fiftieshousewife.undertest.techniques.tags;

/** A shop's delivery charge: £4.99 on baskets under £50, free from £50. */
public final class DeliveryCharge {

    static final long FREE_FROM_PENCE = 5_000;
    static final long CHARGE_PENCE = 499;

    private DeliveryCharge() {
    }

    public static long forBasket(final long basketPence) {
        return basketPence >= FREE_FROM_PENCE ? 0 : CHARGE_PENCE;
    }
}
