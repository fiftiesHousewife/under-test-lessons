package io.github.fiftieshousewife.undertest.techniques.examples;

import io.github.fiftieshousewife.undertest.techniques.examples.fixed.ShopRefundPolicy;
import io.github.fiftieshousewife.undertest.techniques.examples.today.TodaysRefundPolicy;

/** The refund rules under test: the ones the returns team confirmed, or today's (-Pcode=today). */
public final class Policies {

    private Policies() {
    }

    public static RefundPolicy underTest() {
        return "today".equals(System.getProperty("code")) ? new TodaysRefundPolicy() : new ShopRefundPolicy();
    }
}
