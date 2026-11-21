package io.github.fiftieshousewife.undertest.techniques.outsidein;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Reading a request's JSON, and sending a JSON answer. */
public final class Exchanges {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private Exchanges() {
    }

    public static JsonNode body(final HttpExchange exchange) throws IOException {
        return JSON.readTree(exchange.getRequestBody().readAllBytes());
    }

    public static void send(final HttpExchange exchange, final int status, final Map<String, Object> body)
            throws IOException {
        final byte[] bytes = JSON.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
