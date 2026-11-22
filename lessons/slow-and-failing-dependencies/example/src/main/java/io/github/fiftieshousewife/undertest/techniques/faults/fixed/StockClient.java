package io.github.fiftieshousewife.undertest.techniques.faults.fixed;

import io.github.fiftieshousewife.undertest.techniques.faults.StockAnswer;
import io.github.fiftieshousewife.undertest.techniques.faults.StockCheck;
import io.github.fiftieshousewife.undertest.techniques.faults.SupplierAnswers;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

/**
 * FIXED: a connect timeout and a read timeout, so a slow supplier costs the shop a known, short wait, and every
 * failure (too slow, a dropped connection, a broken answer) becomes a clear "supplier unavailable".
 */
public final class StockClient implements StockCheck {

    private final HttpClient http;
    private final URI supplier;
    private final Duration readTimeout;

    public StockClient(final URI supplier, final Duration connectTimeout, final Duration readTimeout) {
        this.http = HttpClient.newBuilder().connectTimeout(connectTimeout).build();
        this.supplier = supplier;
        this.readTimeout = readTimeout;
    }

    @Override
    public StockAnswer check(final String sku) {
        try {
            final HttpRequest request = SupplierAnswers.stockRequest(supplier, sku).timeout(readTimeout).build();
            return SupplierAnswers.read(http.send(request, HttpResponse.BodyHandlers.ofString()));
        } catch (HttpTimeoutException e) {
            return StockAnswer.supplierUnavailable("no answer within " + readTimeout.toMillis() + " ms");
        } catch (IOException e) {
            return StockAnswer.supplierUnavailable("the connection failed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return StockAnswer.supplierUnavailable("the stock check was interrupted");
        }
    }
}
