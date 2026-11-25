package io.github.fiftieshousewife.undertest.techniques.recorded;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import tools.jackson.databind.json.JsonMapper;

/** Asks the supplier's service about a postcode, and reads the town from its JSON answer. */
public final class SupplierHttp {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private final URI base;

    public SupplierHttp(final URI base) {
        this.base = base;
    }

    public HttpResponse<String> get(final String postcode) {
        final String path = "/postcodes/" + URLEncoder.encode(postcode, StandardCharsets.UTF_8).replace("+", "%20");
        final HttpRequest request = HttpRequest.newBuilder(base.resolve(path)).GET().build();
        try {
            return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new SupplierUnavailableException("the postcode supplier can't be reached", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SupplierUnavailableException("interrupted while asking the postcode supplier", e);
        }
    }

    public static String townIn(final String json) {
        return JSON.readTree(json).path("town").asString();
    }
}
