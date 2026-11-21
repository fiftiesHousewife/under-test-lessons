package io.github.fiftieshousewife.undertest.techniques.outsidein;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/** The booking service: the JDK's own small web server, answering POST /bookings. */
public final class BookingServer implements AutoCloseable {

    private final HttpServer server;

    /** Starts the service on a free port on this machine. */
    public BookingServer(final HttpHandler bookings) {
        try {
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        server.createContext("/bookings", bookings);
        server.start();
    }

    public int port() {
        return server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
