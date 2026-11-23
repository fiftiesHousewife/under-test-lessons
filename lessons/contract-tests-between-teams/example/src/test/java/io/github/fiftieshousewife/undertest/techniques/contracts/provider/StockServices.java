package io.github.fiftieshousewife.undertest.techniques.contracts.provider;

import io.github.fiftieshousewife.undertest.techniques.contracts.provider.fixed.AgreedStockBodies;
import io.github.fiftieshousewife.undertest.techniques.contracts.provider.today.TodaysStockBodies;

/** The stock service the tests start: the fixed answers, or today's with -Pcode=today. */
public final class StockServices {

    private StockServices() {
    }

    public static StockServer start(final Shelves shelves) {
        final StockBodies bodies = "today".equals(System.getProperty("code"))
                ? new TodaysStockBodies() : new AgreedStockBodies();
        return new StockServer(shelves, bodies);
    }
}
