package io.github.fiftieshousewife.undertest.techniques.contracts.provider.today;

import io.github.fiftieshousewife.undertest.techniques.contracts.provider.StockBodies;
import java.util.Map;

/** TODAY: a tidy-up renamed "available" to "quantityInStock", and the order screen still reads "available". */
public final class TodaysStockBodies implements StockBodies {

    @Override
    public Map<String, Object> item(final String sku, final int count) {
        return Map.of(
                "sku", sku,
                "quantityInStock", count); // renamed from "available" in a tidy-up
    }

    @Override
    public Map<String, Object> noSuchItem(final String sku) {
        return Map.of("error", "no item " + sku);
    }
}
