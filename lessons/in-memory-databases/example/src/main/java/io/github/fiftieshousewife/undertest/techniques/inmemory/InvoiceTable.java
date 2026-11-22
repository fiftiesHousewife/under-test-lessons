package io.github.fiftieshousewife.undertest.techniques.inmemory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** The invoices table, read and written with plain SQL that every database runs the same way. */
public final class InvoiceTable {

    /** How to ask the database for the next invoice number. */
    @FunctionalInterface
    public interface NextNumberQuery {
        PreparedStatement prepare(Connection connection) throws SQLException;
    }

    private static final String INSERT =
            "INSERT INTO invoices (invoice_number, customer, amount_pence) VALUES (?, ?, ?)";
    private static final String ALL_NUMBERS = "SELECT invoice_number FROM invoices ORDER BY invoice_number";

    private final Connections database;
    private final NextNumberQuery nextNumber;

    public InvoiceTable(final Connections database, final NextNumberQuery nextNumber) {
        this.database = database;
        this.nextNumber = nextNumber;
    }

    /** Works out the next number and inserts the invoice under it, in one transaction. */
    public int issue(final String customer, final long amountInPence) throws SQLException {
        try (Connection connection = database.open()) {
            connection.setAutoCommit(false);
            final int number = next(connection);
            try (PreparedStatement insert = connection.prepareStatement(INSERT)) {
                insert.setInt(1, number);
                insert.setString(2, customer);
                insert.setLong(3, amountInPence);
                insert.executeUpdate();
            }
            connection.commit();
            return number;
        }
    }

    public List<Integer> numbers() throws SQLException {
        try (Connection connection = database.open();
                PreparedStatement select = connection.prepareStatement(ALL_NUMBERS);
                ResultSet rows = select.executeQuery()) {
            final List<Integer> numbers = new ArrayList<>();
            while (rows.next()) {
                numbers.add(rows.getInt(1));
            }
            return numbers;
        }
    }

    private int next(final Connection connection) throws SQLException {
        try (PreparedStatement select = nextNumber.prepare(connection); ResultSet row = select.executeQuery()) {
            row.next();
            return row.getInt(1); // JDBC reads a SQL null as 0
        }
    }
}
