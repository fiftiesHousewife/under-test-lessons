package io.github.fiftieshousewife.undertest.techniques.faults;

import io.github.fiftieshousewife.undertest.techniques.faults.fixed.StockClient;
import io.github.fiftieshousewife.undertest.techniques.faults.today.TodaysStockClient;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

/** The code the tests run against: the fix, or today's with -Pcode=today (and -Pcode=answer, the exercise's). */
public final class Clients {

    /** How long the fixed client waits to connect, and then for an answer. */
    static final Duration TIMEOUT = Duration.ofMillis(500);

    private Clients() {
    }

    public static StockCheck at(final String baseUrl) {
        final URI supplier = URI.create(baseUrl);
        warmUp(supplier);
        return today() ? new TodaysStockClient(supplier) : new StockClient(supplier, TIMEOUT, TIMEOUT);
    }

    /**
     * One untimed request to the stand-in's own admin page, before a test's clock starts. The first HTTP call in a
     * fresh JVM loads the JDK's HTTP client and wakes the stand-in, which in busy continuous integration can take most
     * of the second a test allows; the tests time the shop's waiting, not that start-up.
     */
    private static void warmUp(final URI supplier) {
        final HttpRequest admin = HttpRequest.newBuilder(supplier.resolve("/__admin/mappings")).GET().build();
        try (HttpClient http = HttpClient.newHttpClient()) {
            http.send(admin, HttpResponse.BodyHandlers.discarding());
        } catch (IOException e) {
            throw new IllegalStateException("the stand-in supplier didn't answer before the test", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while waking the stand-in supplier", e);
        }
    }

    /** The answer, or the error's type and message, so a failure reads in one line. */
    public static String outcome(final StockCheck stock, final String sku) {
        try {
            return stock.check(sku).toString();
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    private static boolean today() {
        return Set.of("today", "answer").contains(System.getProperty("code"));
    }
}
