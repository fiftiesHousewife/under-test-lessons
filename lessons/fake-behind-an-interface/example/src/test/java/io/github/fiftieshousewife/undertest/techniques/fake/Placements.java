package io.github.fiftieshousewife.undertest.techniques.fake;

import io.github.fiftieshousewife.undertest.techniques.fake.fixed.FixedOrderPlacement;
import io.github.fiftieshousewife.undertest.techniques.fake.today.TodaysOrderPlacement;

/** The order placement the tests run against: the fixed one, or today's with -Pcode=today. */
final class Placements {

    private Placements() {
    }

    static OrderPlacement using(final MessageQueue queue) {
        return "today".equals(System.getProperty("code"))
                ? new TodaysOrderPlacement(queue) : new FixedOrderPlacement(queue);
    }
}
