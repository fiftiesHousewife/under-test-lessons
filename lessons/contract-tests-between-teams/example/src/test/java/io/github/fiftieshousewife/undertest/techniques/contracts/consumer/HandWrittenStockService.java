package io.github.fiftieshousewife.undertest.techniques.contracts.consumer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/* The order screen team's own stand-in for the stock service: it answers what they expect the real one to send. */
final class HandWrittenStockService implements AutoCloseable {

    private static final String KETTLE = "{\"sku\": \"KETTLE-01\", \"available\": 3}";

    private final HttpServer server;

    HandWrittenStockService() {
        try {
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        server.createContext("/stock/", HandWrittenStockService::answer);
        server.start();
    }

    private static void answer(final HttpExchange exchange) throws IOException {
        final byte[] body = KETTLE.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    URI address() {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort());
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
