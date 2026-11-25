package io.github.fiftieshousewife.undertest.techniques.inmemory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.jdbcx.JdbcDataSource;

/* The team's long-lived test database: one file on disk that every test shares, set up by hand, never emptied. */
final class SharedTestDatabase {

    private static final JdbcDataSource DATABASE = new JdbcDataSource();
    private static boolean setUp;

    private SharedTestDatabase() {
    }

    static synchronized Connections connections() throws SQLException {
        if (!setUp) {
            DATABASE.setURL("jdbc:h2:./build/shared-test-database/invoices");
            setUpByHand();
            setUp = true;
        }
        return DATABASE::getConnection;
    }

    /* A copy of the table typed in by hand, and the test data someone loaded once. */
    private static void setUpByHand() throws SQLException {
        try (Connection connection = DATABASE.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS invoices "
                    + "(invoice_number INTEGER PRIMARY KEY, customer VARCHAR(100), amount_pence BIGINT)");
            try (ResultSet count = statement.executeQuery("SELECT COUNT(*) FROM invoices")) {
                count.next();
                if (count.getInt(1) == 0) {
                    statement.execute("INSERT INTO invoices VALUES "
                            + "(1041, 'Northwind Teas', 12500), (1042, 'Corner Deli', 3400)");
                }
            }
        }
    }
}
