package io.github.fiftieshousewife.undertest.techniques.inmemory.today;

import io.github.fiftieshousewife.undertest.techniques.inmemory.Connections;
import io.github.fiftieshousewife.undertest.techniques.inmemory.InvoiceBook;
import io.github.fiftieshousewife.undertest.techniques.inmemory.InvoiceTable;
import java.sql.SQLException;
import java.util.List;

/**
 * TODAY: the next number is the highest so far, plus one. With no invoices yet, the highest is null, null plus one is
 * null, and null reads as 0: the shop's first invoice is number 0. On a test database that always has rows, no test
 * ever sees it.
 */
public final class TodaysInvoiceBook implements InvoiceBook {

    private static final String NEXT_NUMBER = "SELECT MAX(invoice_number) + 1 FROM invoices";

    private final InvoiceTable table;

    public TodaysInvoiceBook(final Connections database) {
        this.table = new InvoiceTable(database, connection -> connection.prepareStatement(NEXT_NUMBER));
    }

    @Override
    public int issue(final String customer, final long amountInPence) throws SQLException {
        return table.issue(customer, amountInPence);
    }

    @Override
    public List<Integer> numbers() throws SQLException {
        return table.numbers();
    }
}
