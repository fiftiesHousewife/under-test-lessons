package io.github.fiftieshousewife.undertest.techniques.blackbox;

import java.util.Optional;

/** The warehouse, which puts stock aside for an order. */
public interface Warehouse {

    /** Puts the stock aside, and returns the warehouse's id for it; empty when there isn't enough. */
    Optional<String> reserve(String sku, int quantity);
}
