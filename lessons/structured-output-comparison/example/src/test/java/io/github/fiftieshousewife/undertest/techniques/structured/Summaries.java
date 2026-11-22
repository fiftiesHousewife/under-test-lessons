package io.github.fiftieshousewife.undertest.techniques.structured;

import io.github.fiftieshousewife.undertest.techniques.structured.fixed.FixedOrderSummary;
import io.github.fiftieshousewife.undertest.techniques.structured.today.TodaysOrderSummary;
import java.time.Clock;
import java.util.List;

/** The summary the tests run against: the fixed one, or today's with -Pcode=today. Both use the real clock. */
final class Summaries {

    static final List<Line> ORDER = List.of(new Line("Tea", 250, 4), new Line("Biscuits", 120, 1));

    private Summaries() {
    }

    static String jsonForTheOrder() {
        final Clock clock = Clock.systemUTC();
        final OrderSummary summary = "today".equals(System.getProperty("code"))
                ? new TodaysOrderSummary(clock) : new FixedOrderSummary(clock);
        return summary.json(ORDER);
    }
}
