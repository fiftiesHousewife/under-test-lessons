package io.github.fiftieshousewife.undertest.techniques.blackbox;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/** The running order service: its database, its warehouse client and its web server, wired from its settings. */
@Slf4j
public final class ShopApplication implements AutoCloseable {

    private final HttpServer server;

    private ShopApplication(final HttpServer server) {
        this.server = server;
    }

    /** Starts the service from its settings, keeping orders with the records the entry point chose. */
    public static ShopApplication start(final Settings settings, final Function<OrderDatabase, OrderRecords> records) {
        final OrderDatabase database = new OrderDatabase(settings.databaseUrl());
        database.createTables();
        final Warehouse warehouse = new WarehouseClient(settings.warehouseUrl());
        final OrderTaking orders = new OrderTaking(warehouse, records.apply(database));
        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(settings.port()), 0);
            server.createContext("/orders", new OrdersHandler(orders));
            server.start();
            log.info("Order service listening on port {}", server.getAddress().getPort());
            return new ShopApplication(server);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
