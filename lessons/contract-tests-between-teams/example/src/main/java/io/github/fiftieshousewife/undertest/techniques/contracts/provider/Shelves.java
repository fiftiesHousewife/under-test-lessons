package io.github.fiftieshousewife.undertest.techniques.contracts.provider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** The stock service's record of how many of each item are on the shelves, by stock-keeping code. */
public final class Shelves implements StockCounts {

    private final Map<String, Integer> counts = new ConcurrentHashMap<>();

    public void put(final String sku, final int count) {
        counts.put(sku, count);
    }

    @Override
    public Optional<Integer> count(final String sku) {
        return Optional.ofNullable(counts.get(sku));
    }
}
