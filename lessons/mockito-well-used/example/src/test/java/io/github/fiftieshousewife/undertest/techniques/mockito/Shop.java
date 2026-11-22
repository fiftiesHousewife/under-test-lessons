package io.github.fiftieshousewife.undertest.techniques.mockito;

import io.github.fiftieshousewife.undertest.techniques.mockito.fixed.FixedRefunds;
import io.github.fiftieshousewife.undertest.techniques.mockito.today.TodaysRefunds;
import java.util.List;

/** The order the tests start from, and the refunds they run against: the fixed ones, or today's with -Pcode=today. */
final class Shop {

    /** A mug at £12.50 and a teapot at £25.00, paid by the card ending 4242. */
    static final Order MUG_AND_TEAPOT = new Order("card ending 4242",
            List.of(new Line("mug", 1250), new Line("teapot", 2500)));

    private Shop() {
    }

    static Refunds refunds(final OrderBook orders, final CardPayments payments) {
        return "today".equals(System.getProperty("code"))
                ? new TodaysRefunds(orders, payments) : new FixedRefunds(orders, payments);
    }
}
