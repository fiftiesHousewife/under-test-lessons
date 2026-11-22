package io.github.fiftieshousewife.undertest.techniques.jakartaee;

import io.github.fiftieshousewife.undertest.techniques.jakartaee.fixed.StandardCheckout;
import io.github.fiftieshousewife.undertest.techniques.jakartaee.today.TodaysCheckout;

/** The checkout class the tests use: the fixed one, or today's with -Pcode=today. */
final class Checkouts {

    private Checkouts() {
    }

    static boolean today() {
        return "today".equals(System.getProperty("code"));
    }

    static Class<? extends Checkout> chosen() {
        return today() ? TodaysCheckout.class : StandardCheckout.class;
    }

    /** Built by hand, the way a unit test does it: the test picks the rate. */
    static Checkout byHand(final TaxRate rate) {
        return today() ? new TodaysCheckout(rate) : new StandardCheckout(rate);
    }
}
