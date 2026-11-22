package io.github.fiftieshousewife.undertest.techniques.tags;

/** Asks the warehouse before the shop promises an order: can it send this many of an item? */
public final class StockCheck {

    private final Warehouse warehouse;

    public StockCheck(final Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public boolean canSend(final String item, final int quantity) {
        return warehouse.inStock(item) >= quantity;
    }
}
