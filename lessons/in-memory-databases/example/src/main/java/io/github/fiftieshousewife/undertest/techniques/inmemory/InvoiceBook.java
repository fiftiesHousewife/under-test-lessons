package io.github.fiftieshousewife.undertest.techniques.inmemory;

import java.sql.SQLException;
import java.util.List;

/** The shop's invoices, each with its own number, kept in a SQL database. */
public interface InvoiceBook {

    /** Issues an invoice, numbered one more than the last, and returns its number. */
    int issue(String customer, long amountInPence) throws SQLException;

    /** Every invoice's number, lowest first. */
    List<Integer> numbers() throws SQLException;
}
