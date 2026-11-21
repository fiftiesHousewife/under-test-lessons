package io.github.fiftieshousewife.undertest.techniques.dependencies;

import io.github.fiftieshousewife.undertest.techniques.dependencies.fixed.Invoice;
import io.github.fiftieshousewife.undertest.techniques.dependencies.rates.ExchangeRates;
import io.github.fiftieshousewife.undertest.techniques.dependencies.today.TodaysInvoice;

/** The invoice the tests run against: the one that takes its rates, or today's with -Pcode=today. */
final class Invoices {

    private Invoices() {
    }

    /** Today's invoice has nowhere to put the stand-in rates, so it ignores them. */
    static PricedInvoice pricedWith(final ExchangeRates rates) {
        return "today".equals(System.getProperty("code")) ? new TodaysInvoice() : new Invoice(rates);
    }
}
