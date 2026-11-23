package io.github.fiftieshousewife.undertest.techniques.fake;

import io.github.fiftieshousewife.undertest.techniques.fake.fixed.FixedOrderPlacement;
import io.github.fiftieshousewife.undertest.techniques.fake.today.TodaysOrderPlacement;
import java.util.Set;

/**
 * The order placement the tests run against: the fixed one, or today's with -Pcode=today (and -Pcode=answer, the
 * exercise's).
 */
public final class Placements {

    private Placements() {
    }

    public static OrderPlacement using(final MessageQueue queue) {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysOrderPlacement(queue) : new FixedOrderPlacement(queue);
    }
}
