package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import java.util.concurrent.Executor;

/** Sends paid orders to the warehouse, in the background, trying once more if the first try fails. */
public class Dispatcher {

    private final Warehouse warehouse;
    private final Executor background;

    public Dispatcher(final Warehouse warehouse, final Executor background) {
        this.warehouse = warehouse;
        this.background = background;
    }

    public void dispatch(final long orderNumber) {
        background.execute(() -> {
            try {
                warehouse.send(orderNumber);
            } catch (IllegalStateException firstTry) {
                warehouse.send(orderNumber);
            }
        });
    }
}
