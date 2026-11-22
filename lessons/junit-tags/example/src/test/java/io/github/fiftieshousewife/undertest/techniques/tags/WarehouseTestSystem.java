package io.github.fiftieshousewife.undertest.techniques.tags;

import java.time.Duration;
import java.util.Map;

/**
 * Stands in for the warehouse's own test system, which takes seconds to start. The wait is the lesson's honest
 * stand-in for that start-up: nothing else here is slow.
 */
final class WarehouseTestSystem implements Warehouse {

    static final Duration START_UP = Duration.ofSeconds(2);

    private final Map<String, Integer> shelves;

    private WarehouseTestSystem(final Map<String, Integer> shelves) {
        this.shelves = Map.copyOf(shelves);
    }

    static WarehouseTestSystem start(final Map<String, Integer> shelves) throws InterruptedException {
        Thread.sleep(START_UP);
        return new WarehouseTestSystem(shelves);
    }

    @Override
    public int inStock(final String item) {
        return shelves.getOrDefault(item, 0);
    }
}
