package io.github.fiftieshousewife.undertest.techniques.inmemory;

import io.github.fiftieshousewife.undertest.techniques.inmemory.fixed.FixedInvoiceBook;
import io.github.fiftieshousewife.undertest.techniques.inmemory.today.TodaysInvoiceBook;

/** Which invoice book the tests use: the fixed one, or today's with -Pcode=today. */
final class InvoiceBooks {

    private InvoiceBooks() {
    }

    static InvoiceBook on(final Connections database) {
        return "today".equals(System.getProperty("code"))
                ? new TodaysInvoiceBook(database)
                : new FixedInvoiceBook(database);
    }
}
