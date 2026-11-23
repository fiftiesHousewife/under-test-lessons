package io.github.fiftieshousewife.undertest.techniques.extensions;

import io.github.fiftieshousewife.undertest.techniques.extensions.fixed.FixedTradeInvoice;
import io.github.fiftieshousewife.undertest.techniques.extensions.today.TodaysTradeInvoice;
import java.util.Set;

/**
 * The trade invoice the tests run against: the fixed one, or today's with -Pcode=today (and with -Pcode=answer, which
 * runs the exercise's answer beside today's tests).
 */
public final class Invoices {

    private Invoices() {
    }

    public static TradeInvoice current() {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysTradeInvoice() : new FixedTradeInvoice();
    }
}
