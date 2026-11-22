package io.github.fiftieshousewife.undertest.techniques.contracts.consumer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** The order screen's client for the stock service: asks how many of an item are available, by its code. */
public final class StockClient {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private final URI stockService;

    public StockClient(final URI stockService) {
        this.stockService = stockService;
    }

    /** The item's stock level, read from the stock service's answer. */
    public StockLevel stockOf(final String sku) {
        final HttpResponse<String> answer = get(stockService.resolve("/stock/" + sku));
        final JsonNode item = JSON.readTree(answer.body());
        return new StockLevel(item.required("sku").asString(), item.required("available").asInt());
    }

    private static HttpResponse<String> get(final URI uri) {
        try {
            return HTTP.send(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while asking the stock service", e);
        }
    }
}
