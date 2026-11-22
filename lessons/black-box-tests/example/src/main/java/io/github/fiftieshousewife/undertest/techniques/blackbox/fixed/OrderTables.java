package io.github.fiftieshousewife.undertest.techniques.blackbox.fixed;

import io.github.fiftieshousewife.undertest.techniques.blackbox.OrderDatabase;
import io.github.fiftieshousewife.undertest.techniques.blackbox.OrderRecords;
import io.github.fiftieshousewife.undertest.techniques.blackbox.OrderRows;
import io.github.fiftieshousewife.undertest.techniques.blackbox.PlacedOrder;
import java.sql.Connection;
import java.sql.SQLException;

/** FIXED: writes the order and its line in one transaction, and commits it, so both are kept or neither is. */
public final class OrderTables implements OrderRecords {

    private final OrderDatabase database;

    public OrderTables(final OrderDatabase database) {
        this.database = database;
    }

    @Override
    public void save(final PlacedOrder order) {
        try (Connection connection = database.connect()) {
            connection.setAutoCommit(false);
            OrderRows.insert(connection, order);
            connection.commit();
        } catch (SQLException e) {
            throw new IllegalStateException("can't save order " + order.orderId(), e);
        }
    }
}
