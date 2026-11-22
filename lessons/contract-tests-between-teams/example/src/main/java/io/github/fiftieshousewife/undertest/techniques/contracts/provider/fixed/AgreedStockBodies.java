package io.github.fiftieshousewife.undertest.techniques.contracts.provider.fixed;

import io.github.fiftieshousewife.undertest.techniques.contracts.provider.StockBodies;
import java.util.Map;

/** FIXED: keeps "available", which the order screen's pact reads, and sends the new name beside it for now. */
public final class AgreedStockBodies implements StockBodies {

    @Override
    public Map<String, Object> item(final String sku, final int count) {
        return Map.of(
                "sku", sku,
                "available", count, // the order screen's pact reads this
                "quantityInStock", count);
    }

    @Override
    public Map<String, Object> noSuchItem(final String sku) {
        return Map.of("error", "no item " + sku);
    }
}
