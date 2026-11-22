package io.github.fiftieshousewife.undertest.techniques.structured.today;

import io.github.fiftieshousewife.undertest.techniques.structured.Line;
import io.github.fiftieshousewife.undertest.techniques.structured.OrderSummary;
import io.github.fiftieshousewife.undertest.techniques.structured.SummaryJson;
import java.time.Clock;
import java.util.List;

/** TODAY: the total adds up each line's price, and forgets how many were bought. */
public final class TodaysOrderSummary implements OrderSummary {

    private final SummaryJson json;

    public TodaysOrderSummary(final Clock clock) {
        this.json = new SummaryJson(clock);
    }

    @Override
    public String json(final List<Line> lines) {
        return json.write(lines, lines.stream().mapToInt(Line::pricePence).sum());
    }
}
