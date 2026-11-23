package io.github.fiftieshousewife.undertest.techniques.dependencies;

import io.github.fiftieshousewife.undertest.techniques.dependencies.fixed.Invoice;
import io.github.fiftieshousewife.undertest.techniques.dependencies.rates.ExchangeRates;
import io.github.fiftieshousewife.undertest.techniques.dependencies.today.TodaysInvoice;
import java.util.Set;

/**
 * The invoice the tests run against: the one that takes its rates, or today's with -Pcode=today, and with
 * -Pcode=answer, which runs the exercise's answer beside today's test.
 */
public final class Invoices {

    private Invoices() {
    }

    /** Today's invoice has nowhere to put the stand-in rates, so it ignores them. */
    public static PricedInvoice pricedWith(final ExchangeRates rates) {
        final boolean today = Set.of("today", "answer").contains(System.getProperty("code"));
        return today ? new TodaysInvoice() : new Invoice(rates);
    }
}
