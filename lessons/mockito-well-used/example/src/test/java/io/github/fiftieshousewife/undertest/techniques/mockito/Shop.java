package io.github.fiftieshousewife.undertest.techniques.mockito;

import io.github.fiftieshousewife.undertest.techniques.mockito.fixed.FixedRefunds;
import io.github.fiftieshousewife.undertest.techniques.mockito.today.TodaysRefunds;
import java.util.List;
import java.util.Set;

/**
 * The order the tests start from, and the refunds they run against: the fixed ones, or today's with -Pcode=today (and
 * with -Pcode=answer, which runs the exercise's answer beside today's tests).
 */
public final class Shop {

    /** A mug at £12.50 and a teapot at £25.00, paid by the card ending 4242. */
    public static final Order MUG_AND_TEAPOT = new Order("card ending 4242",
            List.of(new Line("mug", 1250), new Line("teapot", 2500)));

    private Shop() {
    }

    public static Refunds refunds(final OrderBook orders, final CardPayments payments) {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysRefunds(orders, payments) : new FixedRefunds(orders, payments);
    }
}
