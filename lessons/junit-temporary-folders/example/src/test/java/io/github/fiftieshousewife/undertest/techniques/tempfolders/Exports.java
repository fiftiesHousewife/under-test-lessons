package io.github.fiftieshousewife.undertest.techniques.tempfolders;

import io.github.fiftieshousewife.undertest.techniques.tempfolders.fixed.FixedOrdersExport;
import io.github.fiftieshousewife.undertest.techniques.tempfolders.today.TodaysOrdersExport;
import java.util.Set;

/**
 * The export the tests run against: the fixed one, or today's with -Pcode=today (and with -Pcode=answer, which runs
 * the exercise's answer beside today's tests).
 */
public final class Exports {

    private Exports() {
    }

    public static OrdersExport current() {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysOrdersExport() : new FixedOrdersExport();
    }
}
