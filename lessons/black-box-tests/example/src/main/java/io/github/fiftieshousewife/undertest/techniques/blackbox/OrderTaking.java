package io.github.fiftieshousewife.undertest.techniques.blackbox;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** The shop's rules for an order: put the stock aside at the warehouse, then keep the order. */
public final class OrderTaking {

    private final AtomicLong nextNumber = new AtomicLong(1001);
    private final Warehouse warehouse;
    private final OrderRecords records;

    public OrderTaking(final Warehouse warehouse, final OrderRecords records) {
        this.warehouse = warehouse;
        this.records = records;
    }

    /** Places the order, or returns empty when the warehouse hasn't enough stock. */
    public Optional<PlacedOrder> place(final OrderRequest request) {
        return warehouse.reserve(request.sku(), request.quantity()).map(reservation -> {
            final PlacedOrder order = new PlacedOrder("O-" + nextNumber.getAndIncrement(), request, reservation);
            records.save(order);
            return order;
        });
    }
}
