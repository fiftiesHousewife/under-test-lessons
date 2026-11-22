package io.github.fiftieshousewife.undertest.techniques.contracts.provider;

import java.util.Map;

/** What the stock service sends back, as JSON fields: an item it stocks, or the answer for a code it doesn't know. */
public interface StockBodies {

    Map<String, Object> item(String sku, int count);

    Map<String, Object> noSuchItem(String sku);
}
