package io.github.fiftieshousewife.undertest.techniques.capture;

import jakarta.servlet.DispatcherType;
import java.net.URI;
import java.util.EnumSet;
import org.eclipse.jetty.ee11.servlet.FilterHolder;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.servlet.LogbookFilter;

/** The old service on an embedded Jetty server, on a free port, with Logbook's servlet filter in front of it. */
public final class CustomerServer implements AutoCloseable {

    private final Server server = new Server();
    private final ServerConnector connector;

    public CustomerServer(final CustomerServlet service, final Logbook logbook) {
        final HttpConfiguration settings = new HttpConfiguration();
        settings.setSendServerVersion(false);
        connector = new ServerConnector(server, new HttpConnectionFactory(settings));
        connector.setPort(0);
        connector.setHost("localhost");
        server.addConnector(connector);
        final ServletContextHandler context = new ServletContextHandler();
        context.addFilter(new FilterHolder(new LogbookFilter(logbook)), "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addServlet(new ServletHolder(service), "/customers/*");
        server.setHandler(context);
    }

    /** Starts the server, and returns its address. */
    public URI start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new IllegalStateException("the customer service could not start", e);
        }
        return URI.create("http://localhost:" + connector.getLocalPort());
    }

    @Override
    public void close() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new IllegalStateException("the customer service could not stop", e);
        }
    }
}
