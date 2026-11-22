package io.github.fiftieshousewife.undertest.techniques.inmemory.fixed;

import io.github.fiftieshousewife.undertest.techniques.inmemory.Connections;
import io.github.fiftieshousewife.undertest.techniques.inmemory.InvoiceBook;
import io.github.fiftieshousewife.undertest.techniques.inmemory.InvoiceTable;
import java.sql.SQLException;
import java.util.List;

/** FIXED: with no invoices yet, the highest number counts as 0, so the first invoice is number 1. Plain SQL. */
public final class FixedInvoiceBook implements InvoiceBook {

    private static final String NEXT_NUMBER = "SELECT COALESCE(MAX(invoice_number), 0) + 1 FROM invoices";

    private final InvoiceTable table;

    public FixedInvoiceBook(final Connections database) {
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
