package io.github.fiftieshousewife.undertest.techniques.blackbox;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import tools.jackson.databind.json.JsonMapper;

/** The warehouse's web service: POST /reservations, answered 201 Created with an id, or 409 Conflict. */
public final class WarehouseClient implements Warehouse {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private final URI base;

    public WarehouseClient(final URI base) {
        this.base = base;
    }

    @Override
    public Optional<String> reserve(final String sku, final int quantity) {
        final String body = JSON.createObjectNode().put("sku", sku).put("quantity", quantity).toString();
        final HttpResponse<String> answer = exchange(HttpRequest.newBuilder(base.resolve("/reservations"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build());
        if (answer.statusCode() == 409) {
            return Optional.empty();
        }
        if (answer.statusCode() != 201) {
            throw new IllegalStateException("the warehouse answered " + answer.statusCode());
        }
        return Optional.of(JSON.readTree(answer.body()).path("reservationId").asString());
    }

    private HttpResponse<String> exchange(final HttpRequest request) {
        try {
            return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException("the warehouse can't be reached", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while asking the warehouse", e);
        }
    }
}
