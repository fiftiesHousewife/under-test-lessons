package io.github.fiftieshousewife.undertest.techniques.contracts.provider;

import java.util.Optional;

/** Where the stock service looks up how many of an item it has, by stock-keeping code. */
public interface StockCounts {

    Optional<Integer> count(String sku);
}
