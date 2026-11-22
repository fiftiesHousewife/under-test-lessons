package io.github.fiftieshousewife.undertest.techniques.faults;

import io.github.fiftieshousewife.undertest.techniques.faults.fixed.StockClient;
import io.github.fiftieshousewife.undertest.techniques.faults.today.TodaysStockClient;
import java.net.URI;
import java.time.Duration;

/** The code the tests run against: the fix, or today's with -Pcode=today. */
final class Clients {

    /** How long the fixed client waits to connect, and then for an answer. */
    static final Duration TIMEOUT = Duration.ofMillis(500);

    private Clients() {
    }

    static StockCheck at(final String baseUrl) {
        final URI supplier = URI.create(baseUrl);
        return today() ? new TodaysStockClient(supplier) : new StockClient(supplier, TIMEOUT, TIMEOUT);
    }

    /** The answer, or the error's type and message, so a failure reads in one line. */
    static String outcome(final StockCheck stock, final String sku) {
        try {
            return stock.check(sku).toString();
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    private static boolean today() {
        return "today".equals(System.getProperty("code"));
    }
}
