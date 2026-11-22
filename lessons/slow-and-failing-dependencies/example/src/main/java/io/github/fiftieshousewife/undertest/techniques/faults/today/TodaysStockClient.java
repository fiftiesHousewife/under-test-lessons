package io.github.fiftieshousewife.undertest.techniques.faults.today;

import io.github.fiftieshousewife.undertest.techniques.faults.StockAnswer;
import io.github.fiftieshousewife.undertest.techniques.faults.StockCheck;
import io.github.fiftieshousewife.undertest.techniques.faults.SupplierAnswers;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * TODAY: no timeout, so a slow supplier holds up the shop for as long as it likes, and a dropped connection or a
 * broken answer escapes as an exception, so the customer sees an error page.
 */
public final class TodaysStockClient implements StockCheck {

    private final HttpClient http = HttpClient.newHttpClient();
    private final URI supplier;

    public TodaysStockClient(final URI supplier) {
        this.supplier = supplier;
    }

    @Override
    public StockAnswer check(final String sku) {
        try {
            final HttpRequest request = SupplierAnswers.stockRequest(supplier, sku).build();
            return SupplierAnswers.read(http.send(request, HttpResponse.BodyHandlers.ofString()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
