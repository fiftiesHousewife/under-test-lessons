package io.github.fiftieshousewife.undertest.techniques.examples;

import io.github.fiftieshousewife.undertest.techniques.examples.fixed.ShopRefundPolicy;
import io.github.fiftieshousewife.undertest.techniques.examples.today.TodaysRefundPolicy;

/** The refund rules under test: the ones the returns team confirmed, or today's (-Pcode=today). */
final class Policies {

    private Policies() {
    }

    static RefundPolicy underTest() {
        return "today".equals(System.getProperty("code")) ? new TodaysRefundPolicy() : new ShopRefundPolicy();
    }
}
