package io.github.fiftieshousewife.undertest.techniques.jakartaee;

import io.github.fiftieshousewife.undertest.techniques.jakartaee.fixed.StandardCheckout;
import io.github.fiftieshousewife.undertest.techniques.jakartaee.today.TodaysCheckout;
import java.util.Set;

/** The checkout class the tests use: the fixed one, or today's with -Pcode=today (and -Pcode=answer, the exercise). */
public final class Checkouts {

    private Checkouts() {
    }

    static boolean today() {
        return Set.of("today", "answer").contains(System.getProperty("code"));
    }

    public static Class<? extends Checkout> chosen() {
        return today() ? TodaysCheckout.class : StandardCheckout.class;
    }

    /** Built by hand, the way a unit test does it: the test picks the rate. */
    static Checkout byHand(final TaxRate rate) {
        return today() ? new TodaysCheckout(rate) : new StandardCheckout(rate);
    }
}
