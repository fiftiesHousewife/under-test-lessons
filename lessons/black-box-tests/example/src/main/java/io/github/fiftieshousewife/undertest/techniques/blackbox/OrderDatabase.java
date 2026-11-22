package io.github.fiftieshousewife.undertest.techniques.blackbox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** The service's own database: it creates its tables when it starts, if they aren't there. */
public final class OrderDatabase {

    private final String url;

    public OrderDatabase(final String url) {
        this.url = url;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /** Creates the orders table, and the order lines table, if they aren't there. */
    public void createTables() {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS orders "
                    + "(order_id VARCHAR(20) PRIMARY KEY, customer VARCHAR(20), reservation_id VARCHAR(20))");
            statement.execute("CREATE TABLE IF NOT EXISTS order_lines "
                    + "(order_id VARCHAR(20), sku VARCHAR(20), quantity INT)");
        } catch (SQLException e) {
            throw new IllegalStateException("can't create the order tables", e);
        }
    }
}
