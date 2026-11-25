package io.github.fiftieshousewife.undertest.techniques.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Customers and the date of their last order, kept in a SQL database. */
public final class CustomerRepository {

    public static final String TODAYS_QUERY =
            // TODAY: oldest order first. On some databases, customers who never ordered come first; on others, last.
            "SELECT name FROM customers ORDER BY last_order";

    public static final String FIXED_QUERY =
            // FIXED: customers who never ordered come first (NULLS FIRST: PostgreSQL, H2, Oracle; not MySQL).
            "SELECT name FROM customers ORDER BY last_order NULLS FIRST";

    /** Where connections come from: the database's own data source, in production and in tests. */
    @FunctionalInterface
    public interface Connections {
        Connection open() throws SQLException;
    }

    private final Connections database;
    private final boolean fixed;

    /** A repository using the fixed query, or today's. */
    public CustomerRepository(final Connections database, final boolean fixed) {
        this.database = database;
        this.fixed = fixed;
    }

    public void createTable() throws SQLException {
        try (Connection connection = database.open(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE customers (name VARCHAR(100) PRIMARY KEY, last_order DATE)");
        }
    }

    public void add(final String name, final LocalDate lastOrder) throws SQLException {
        try (Connection connection = database.open();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO customers VALUES (?, ?)")) {
            insert.setString(1, name);
            insert.setObject(2, lastOrder);
            insert.executeUpdate();
        }
    }

    /** Who the sales team should call first: those who never ordered, then those who ordered longest ago. */
    public List<String> whoToCallFirst() throws SQLException {
        try (Connection connection = database.open();
                PreparedStatement select = select(connection);
                ResultSet rows = select.executeQuery()) {
            final List<String> names = new ArrayList<>();
            while (rows.next()) {
                names.add(rows.getString("name"));
            }
            return names;
        }
    }

    private PreparedStatement select(final Connection connection) throws SQLException {
        return fixed ? connection.prepareStatement(FIXED_QUERY) : connection.prepareStatement(TODAYS_QUERY);
    }
}
