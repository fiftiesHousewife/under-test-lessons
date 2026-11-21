package io.github.fiftieshousewife.undertest.techniques.structured;

import java.util.List;

/** An order summary, as JSON, sent to the warehouse. */
public interface OrderSummary {

    String json(List<Line> lines);
}
