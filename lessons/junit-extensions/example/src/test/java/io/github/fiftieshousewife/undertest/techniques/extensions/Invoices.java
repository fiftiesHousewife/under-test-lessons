package io.github.fiftieshousewife.undertest.techniques.extensions;

import io.github.fiftieshousewife.undertest.techniques.extensions.fixed.FixedTradeInvoice;
import io.github.fiftieshousewife.undertest.techniques.extensions.today.TodaysTradeInvoice;

/** The trade invoice the tests run against: the fixed one, or today's with -Pcode=today. */
final class Invoices {

    private Invoices() {
    }

    static TradeInvoice current() {
        return "today".equals(System.getProperty("code")) ? new TodaysTradeInvoice() : new FixedTradeInvoice();
    }
}
