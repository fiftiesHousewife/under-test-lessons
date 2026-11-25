package io.github.fiftieshousewife.undertest.techniques.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* AFTER: each test gets a new in-memory database with the real schema, so no leftover row can hide a bug. */
class FreshDatabaseTest {

    private FreshDatabase database;
    private InvoiceBook invoices;

    @BeforeEach
    void startAFreshDatabase() throws SQLException {
        database = FreshDatabase.start();
        invoices = InvoiceBooks.on(database.connections());
    }

    @AfterEach
    void throwItAway() throws SQLException {
        database.close();
    }

    @Test
    void eachTestStartsWithNoInvoices() throws SQLException {
        assertThat(invoices.numbers()).as("invoices before the test").isEmpty();
    }

    @Test
    void theShopsFirstInvoiceIsNumberOne() throws SQLException {
        assertThat(invoices.issue("Hill Farm Shop", 4_250)).as("the first invoice's number").isEqualTo(1);
    }

    @Test
    void invoicesAreNumberedOneTwoThree() throws SQLException {
        invoices.issue("Hill Farm Shop", 4_250);
        invoices.issue("Corner Deli", 780);
        invoices.issue("Hill Farm Shop", 1_999);

        assertThat(invoices.numbers()).as("invoice numbers").isEqualTo(List.of(1, 2, 3));
    }
}
