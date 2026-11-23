package io.github.fiftieshousewife.undertest.techniques.inmemory;

import io.github.fiftieshousewife.undertest.techniques.inmemory.fixed.FixedInvoiceBook;
import io.github.fiftieshousewife.undertest.techniques.inmemory.today.TodaysInvoiceBook;
import java.util.Set;

/** Which invoice book the tests use: the fixed one, or today's with -Pcode=today (and -Pcode=answer, the exercise). */
public final class InvoiceBooks {

    private InvoiceBooks() {
    }

    public static InvoiceBook on(final Connections database) {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysInvoiceBook(database)
                : new FixedInvoiceBook(database);
    }
}
