package io.github.fiftieshousewife.undertest.techniques.tempfolders;

import io.github.fiftieshousewife.undertest.techniques.tempfolders.fixed.FixedOrdersExport;
import io.github.fiftieshousewife.undertest.techniques.tempfolders.today.TodaysOrdersExport;

/** The export the tests run against: the fixed one, or today's with -Pcode=today. */
final class Exports {

    private Exports() {
    }

    static OrdersExport current() {
        return "today".equals(System.getProperty("code")) ? new TodaysOrdersExport() : new FixedOrdersExport();
    }
}
