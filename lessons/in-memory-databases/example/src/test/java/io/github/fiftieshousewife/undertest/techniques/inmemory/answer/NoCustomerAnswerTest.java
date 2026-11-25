package io.github.fiftieshousewife.undertest.techniques.inmemory.answer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.fiftieshousewife.undertest.techniques.inmemory.FreshDatabase;
import io.github.fiftieshousewife.undertest.techniques.inmemory.InvoiceBooks;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: the real schema says every invoice has a customer (NOT NULL). The shared test database's
 * table, typed in by hand, left that rule out; a fresh database has it, so an invoice with no customer is refused.
 * Run it with today's invoice book with {@code -Pcode=answer}.
 */
class NoCustomerAnswerTest {

    @Test
    void anInvoiceWithNoCustomerIsRefused() throws SQLException {
        try (FreshDatabase database = FreshDatabase.start()) {
            assertThatThrownBy(() -> InvoiceBooks.on(database.connections()).issue(null, 4_250))
                    .as("an invoice with no customer")
                    .isInstanceOf(SQLIntegrityConstraintViolationException.class)
                    .hasMessageContaining("customer");
        }
    }
}
