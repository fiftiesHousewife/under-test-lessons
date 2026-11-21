package io.github.fiftieshousewife.undertest.techniques.structured.fixed;

import io.github.fiftieshousewife.undertest.techniques.structured.Line;
import io.github.fiftieshousewife.undertest.techniques.structured.OrderSummary;
import io.github.fiftieshousewife.undertest.techniques.structured.SummaryJson;
import java.util.List;

/** FIXED: the total is price times quantity, for every line. */
public final class FixedOrderSummary implements OrderSummary {

    private final SummaryJson json;

    public FixedOrderSummary(final SummaryJson json) {
        this.json = json;
    }

    @Override
    public String json(final List<Line> lines) {
        return json.write(lines, lines.stream().mapToInt(line -> line.pricePence() * line.quantity()).sum());
    }
}
