package io.github.fiftieshousewife.undertest.techniques.examples.today;

import io.github.fiftieshousewife.undertest.techniques.examples.Refund;
import io.github.fiftieshousewife.undertest.techniques.examples.RefundPolicy;
import io.github.fiftieshousewife.undertest.techniques.examples.ReturnedItem;
import java.time.temporal.ChronoUnit;

/** TODAY: the refunds the tills give. "Within 30 days" is counted as fewer than 30 days after buying. */
public final class TodaysRefundPolicy implements RefundPolicy {

    private static final int RETURN_DAYS = 30;

    @Override
    public Refund refundFor(final ReturnedItem item) {
        final long days = ChronoUnit.DAYS.between(item.bought(), item.returned());
        final boolean inTime = days < RETURN_DAYS;
        if (inTime && item.receipt()) {
            return Refund.TO_CARD;
        }
        return inTime || item.receipt() ? Refund.STORE_CREDIT : Refund.NOTHING;
    }
}
