package io.github.fiftieshousewife.undertest.techniques.examples.fixed;

import io.github.fiftieshousewife.undertest.techniques.examples.Refund;
import io.github.fiftieshousewife.undertest.techniques.examples.RefundPolicy;
import io.github.fiftieshousewife.undertest.techniques.examples.ReturnedItem;
import java.time.temporal.ChronoUnit;

/** FIXED: asked about their example, the returns team confirmed it: the 30 days include the 30th day. */
public final class ShopRefundPolicy implements RefundPolicy {

    private static final int RETURN_DAYS = 30;

    @Override
    public Refund refundFor(final ReturnedItem item) {
        final long days = ChronoUnit.DAYS.between(item.bought(), item.returned());
        final boolean inTime = days <= RETURN_DAYS;
        if (inTime && item.receipt()) {
            return Refund.TO_CARD;
        }
        return inTime || item.receipt() ? Refund.STORE_CREDIT : Refund.NOTHING;
    }
}
