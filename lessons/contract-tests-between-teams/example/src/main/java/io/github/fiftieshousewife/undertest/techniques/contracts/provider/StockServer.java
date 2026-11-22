package io.github.fiftieshousewife.undertest.techniques.contracts.provider;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import tools.jackson.databind.json.JsonMapper;

/** The stock service: the JDK's own small web server, answering GET /stock/{sku} from what is on the shelves. */
public final class StockServer implements AutoCloseable {

    private static final String PATH = "/stock/";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final HttpServer server;
    private final StockCounts shelves;
    private final StockBodies bodies;

    /** Starts the service on a free port on this machine, reading these shelves. */
    public StockServer(final StockCounts shelves, final StockBodies bodies) {
        this.shelves = shelves;
        this.bodies = bodies;
        try {
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        server.createContext(PATH, this::answer);
        server.start();
    }

    private void answer(final HttpExchange exchange) throws IOException {
        final String sku = exchange.getRequestURI().getPath().substring(PATH.length());
        final Optional<Integer> count = shelves.count(sku);
        if (count.isPresent()) {
            send(exchange, 200, bodies.item(sku, count.get()));
        } else {
            send(exchange, 404, bodies.noSuchItem(sku));
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

    public int port() {
        return server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
