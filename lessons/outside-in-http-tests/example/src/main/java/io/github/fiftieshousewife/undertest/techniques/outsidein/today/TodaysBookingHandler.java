package io.github.fiftieshousewife.undertest.techniques.outsidein.today;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.fiftieshousewife.undertest.techniques.outsidein.BookingRules;
import io.github.fiftieshousewife.undertest.techniques.outsidein.Exchanges;
import java.io.IOException;
import java.util.Map;

/** TODAY: always answers 200 OK, even when the booking was refused; the error is only in the body. */
public final class TodaysBookingHandler implements HttpHandler {

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final int nights = Exchanges.body(exchange).path("nights").asInt();
        try {
            Exchanges.send(exchange, 200, Map.of("nights", nights, "pricePence", BookingRules.pricePence(nights)));
        } catch (IllegalArgumentException e) {
            Exchanges.send(exchange, 200, Map.of("error", e.getMessage()));
        }
    }
}
