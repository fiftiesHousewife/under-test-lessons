package io.github.fiftieshousewife.undertest.techniques.openapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import tools.jackson.databind.json.JsonMapper;

/** The parcel tracking service: the JDK's own small web server, answering GET /parcels/{parcelId}. */
public final class ParcelServer implements AutoCloseable {

    private static final String PATH = "/parcels/";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final HttpServer server;
    private final Map<String, Parcel> parcels;
    private final ParcelBodies bodies;

    /** Starts the service on a free port on this machine, knowing these parcels. */
    public ParcelServer(final List<Parcel> known, final ParcelBodies bodies) {
        this.parcels = known.stream().collect(Collectors.toUnmodifiableMap(Parcel::id, Function.identity()));
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
        final String parcelId = exchange.getRequestURI().getPath().substring(PATH.length());
        final Optional<Parcel> parcel = Optional.ofNullable(parcels.get(parcelId));
        if (parcel.isPresent()) {
            send(exchange, 200, bodies.parcel(parcel.get()));
        } else {
            send(exchange, 404, bodies.noSuchParcel(parcelId));
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
