package io.github.fiftieshousewife.undertest.techniques.extensions;

import io.github.fiftieshousewife.undertest.techniques.extensions.fixed.FixedDeliveryNote;
import io.github.fiftieshousewife.undertest.techniques.extensions.fixed.FixedTradeInvoice;
import io.github.fiftieshousewife.undertest.techniques.extensions.today.TodaysDeliveryNote;
import io.github.fiftieshousewife.undertest.techniques.extensions.today.TodaysTradeInvoice;
import java.util.Set;

/**
 * The trade invoice and delivery note the tests run against: the fixed ones, or today's with -Pcode=today (and with
 * -Pcode=answer, which runs the exercise's answer beside today's tests).
 */
public final class Invoices {

    private Invoices() {
    }

    public static TradeInvoice current() {
        return today() ? new TodaysTradeInvoice() : new FixedTradeInvoice();
    }

    public static DeliveryNote deliveryNote() {
        return today() ? new TodaysDeliveryNote() : new FixedDeliveryNote();
    }

    private static boolean today() {
        return Set.of("today", "answer").contains(System.getProperty("code"));
    }
}
