package io.github.fiftieshousewife.undertest.techniques.capture;

import com.sun.net.httpserver.HttpServer;
import io.github.fiftieshousewife.undertest.techniques.capture.fixed.FilteredCapture;
import io.github.fiftieshousewife.undertest.techniques.capture.today.TodaysCapture;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.zalando.logbook.Logbook;

/**
 * The old service running with its capture switched on, and a stand-in loyalty provider on the same machine. The
 * capture is the fix, or today's with -Pcode=today.
 */
public final class OldService implements AutoCloseable {

    public static final String CALLER_TOKEN =
            // The token a caller sends the old service, and the key the old service sends the loyalty provider.
            "Bearer caller-7f3a9c";
    static final String LOYALTY_KEY = "loyalty-51d2";
    static final String EMAIL = "jo.bloggs@example.com";

    private final CaptureLog captureLog = new CaptureLog();
    private final HttpServer provider = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
    private final LoyaltyClient loyalty;
    private final CustomerServer server;
    private final URI address;

    public OldService() throws IOException {
        provider.createContext("/points", exchange -> {
            final byte[] answer = "{\"points\":120}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, answer.length);
            try (OutputStream body = exchange.getResponseBody()) {
                body.write(answer);
            }
        });
        provider.start();
        final Logbook logbook = chosenCapture().logbook(captureLog);
        loyalty = new LoyaltyClient(URI.create("http://localhost:" + provider.getAddress().getPort()), LOYALTY_KEY,
                logbook);
        server = new CustomerServer(new CustomerServlet(Map.of("C-1001", new Customer("C-1001", EMAIL, "gold")),
                loyalty), logbook);
        address = server.start();
    }

    /** Asks the old service for a customer, as a caller with a token would; returns its answer's body. */
    public String customer(final String id) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(address.resolve("/customers/" + id))
                .header("Authorization", CALLER_TOKEN).GET().build();
        try (HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()) {
            return http.send(request, HttpResponse.BodyHandlers.ofString()).body();
        }
    }

    /** Everything the capture has written. */
    public String captured() {
        return captureLog.text();
    }

    @Override
    public void close() throws IOException {
        server.close();
        loyalty.close();
        provider.stop(0);
    }

    private static Capture chosenCapture() {
        return "today".equals(System.getProperty("code")) ? new TodaysCapture() : new FilteredCapture();
    }
}
