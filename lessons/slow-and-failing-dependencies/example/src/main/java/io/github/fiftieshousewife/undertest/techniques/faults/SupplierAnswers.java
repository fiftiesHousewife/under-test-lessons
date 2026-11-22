package io.github.fiftieshousewife.undertest.techniques.faults;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import tools.jackson.databind.json.JsonMapper;

/** The supplier's stock address, and how to read its answer: 200 with the units as JSON, or an error status. */
public final class SupplierAnswers {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private SupplierAnswers() {
    }

    /** A request for one item's stock, over HTTP/1.1 as the supplier speaks it, to build on. */
    public static HttpRequest.Builder stockRequest(final URI supplier, final String sku) {
        return HttpRequest.newBuilder(supplier.resolve("/stock/" + sku)).version(HttpClient.Version.HTTP_1_1).GET();
    }

    /** The units from a 200 answer; any other status means the supplier is unavailable. */
    public static StockAnswer read(final HttpResponse<String> answer) {
        if (answer.statusCode() != 200) {
            return StockAnswer.supplierUnavailable("the supplier answered " + answer.statusCode());
        }
        return StockAnswer.inStock(JSON.readTree(answer.body()).path("units").asInt());
    }
}
