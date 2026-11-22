package io.github.fiftieshousewife.undertest.techniques.blackbox.today;

import io.github.fiftieshousewife.undertest.techniques.blackbox.OrderDatabase;
import io.github.fiftieshousewife.undertest.techniques.blackbox.OrderRecords;
import io.github.fiftieshousewife.undertest.techniques.blackbox.OrderRows;
import io.github.fiftieshousewife.undertest.techniques.blackbox.PlacedOrder;
import java.sql.Connection;
import java.sql.SQLException;

/** TODAY: writes the order and its line in one transaction, and never commits it, so the database throws both away. */
public final class TodaysOrderTables implements OrderRecords {

    private final OrderDatabase database;

    public TodaysOrderTables(final OrderDatabase database) {
        this.database = database;
    }

    @Override
    public void save(final PlacedOrder order) {
        try (Connection connection = database.connect()) {
            connection.setAutoCommit(false);
            OrderRows.insert(connection, order);
        } catch (SQLException e) {
            throw new IllegalStateException("can't save order " + order.orderId(), e);
        }
    }
}
