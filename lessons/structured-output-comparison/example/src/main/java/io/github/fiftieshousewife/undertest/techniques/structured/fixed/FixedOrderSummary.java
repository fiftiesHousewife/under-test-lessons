package io.github.fiftieshousewife.undertest.techniques.structured.fixed;

import io.github.fiftieshousewife.undertest.techniques.structured.Line;
import io.github.fiftieshousewife.undertest.techniques.structured.OrderSummary;
import io.github.fiftieshousewife.undertest.techniques.structured.SummaryJson;
import java.time.Clock;
import java.util.List;

/** FIXED: the total is price times quantity, for every line. */
public final class FixedOrderSummary implements OrderSummary {

    private final SummaryJson json;

    public FixedOrderSummary(final Clock clock) {
        this.json = new SummaryJson(clock);
    }

    @Override
    public String json(final List<Line> lines) {
        return json.write(lines, lines.stream().mapToInt(line -> line.pricePence() * line.quantity()).sum());
    }
}
