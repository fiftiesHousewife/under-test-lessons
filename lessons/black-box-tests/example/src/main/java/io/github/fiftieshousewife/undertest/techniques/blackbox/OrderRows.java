package io.github.fiftieshousewife.undertest.techniques.blackbox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** The two rows an order is kept as: the order, and its line. */
public final class OrderRows {

    private OrderRows() {
    }

    public static void insert(final Connection connection, final PlacedOrder order) throws SQLException {
        try (PreparedStatement orders = connection.prepareStatement(
                "INSERT INTO orders (order_id, customer, reservation_id) VALUES (?, ?, ?)");
             PreparedStatement lines = connection.prepareStatement(
                "INSERT INTO order_lines (order_id, sku, quantity) VALUES (?, ?, ?)")) {
            orders.setString(1, order.orderId());
            orders.setString(2, order.request().customer());
            orders.setString(3, order.reservationId());
            orders.executeUpdate();
            lines.setString(1, order.orderId());
            lines.setString(2, order.request().sku());
            lines.setInt(3, order.request().quantity());
            lines.executeUpdate();
        }
    }
}
