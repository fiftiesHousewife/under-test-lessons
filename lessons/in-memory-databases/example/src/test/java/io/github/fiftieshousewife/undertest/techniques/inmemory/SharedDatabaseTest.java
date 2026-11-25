package io.github.fiftieshousewife.undertest.techniques.inmemory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

/* BEFORE: every test uses one long-lived test database, set up by hand, and leaves its rows behind for the next. */
class SharedDatabaseTest {

    @Test
    void newInvoicesAreNumberedInTurn() throws SQLException {
        final InvoiceBook invoices = InvoiceBooks.on(SharedTestDatabase.connections());
        final int first = invoices.issue("Hill Farm Shop", 4_250);
        final int second = invoices.issue("Hill Farm Shop", 1_999);

        assertAll(
                () -> assertThat(first).as("a new invoice's number").isPositive(),
                () -> assertThat(second).as("the next invoice's number").isEqualTo(first + 1));
    }

    @Test
    void anIssuedInvoiceIsKept() throws SQLException {
        final InvoiceBook invoices = InvoiceBooks.on(SharedTestDatabase.connections());
        final int number = invoices.issue("Corner Deli", 780);

        assertThat(invoices.numbers()).as("invoice numbers").contains(number);
    }
}
