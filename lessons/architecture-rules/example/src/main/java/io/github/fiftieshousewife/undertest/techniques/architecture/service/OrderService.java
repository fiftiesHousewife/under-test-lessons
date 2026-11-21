package io.github.fiftieshousewife.undertest.techniques.architecture.service;

import io.github.fiftieshousewife.undertest.techniques.architecture.storage.OrderStore;

/** The service layer: the shop's rules. The web layer goes through here, never straight to storage. */
public final class OrderService {

    private final OrderStore store;

    public OrderService(final OrderStore store) {
        this.store = store;
    }

    public void placeOrder(final String id, final int amountPence) {
        if (amountPence <= 0) {
            throw new IllegalArgumentException("an order must cost something");
        }
        store.save(id, amountPence);
    }

    public int orderCount() {
        return store.count();
    }
}
