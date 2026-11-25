package io.github.fiftieshousewife.undertest.techniques.outsidein.fixed;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.fiftieshousewife.undertest.techniques.outsidein.BookingRules;
import io.github.fiftieshousewife.undertest.techniques.outsidein.Exchanges;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import tools.jackson.databind.JsonNode;

/**
 * FIXED: 201 Created with the new booking's address, or 400 Bad Request when the booking is refused, with the reason:
 * no nights field at all, or too few nights.
 */
public final class BookingHandler implements HttpHandler {

    private final AtomicInteger lastId = new AtomicInteger();

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final JsonNode body = Exchanges.body(exchange);
        if (!body.has("nights")) {
            Exchanges.send(exchange, 400, Map.of("error", "nights is required"));
            return;
        }
        final int nights = body.path("nights").asInt();
        try {
            final int price = BookingRules.pricePence(nights);
            exchange.getResponseHeaders().set("Location", "/bookings/" + lastId.incrementAndGet());
            Exchanges.send(exchange, 201, Map.of("nights", nights, "pricePence", price));
        } catch (IllegalArgumentException e) {
            Exchanges.send(exchange, 400, Map.of("error", e.getMessage()));
        }
    }
}
