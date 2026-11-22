package io.github.fiftieshousewife.undertest.techniques.blackbox;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** POST /orders: places the order, and answers 201 Created with its id, or 409 Conflict when stock is short. */
public final class OrdersHandler implements HttpHandler {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final OrderTaking orders;

    public OrdersHandler(final OrderTaking orders) {
        this.orders = orders;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final JsonNode body = JSON.readTree(exchange.getRequestBody().readAllBytes());
        final OrderRequest request = new OrderRequest(body.path("customer").asString(),
                body.path("sku").asString(), body.path("quantity").asInt());
        final Optional<PlacedOrder> placed = orders.place(request);
        if (placed.isPresent()) {
            exchange.getResponseHeaders().set("Location", "/orders/" + placed.get().orderId());
            send(exchange, 201, Map.of("orderId", placed.get().orderId()));
        } else {
            send(exchange, 409, Map.of("error", "not enough stock of " + request.sku()));
        }
    }

    private static void send(final HttpExchange exchange, final int status, final Map<String, Object> body)
            throws IOException {
        final byte[] bytes = JSON.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
