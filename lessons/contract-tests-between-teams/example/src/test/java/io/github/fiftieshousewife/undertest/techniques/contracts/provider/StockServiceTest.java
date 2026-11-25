package io.github.fiftieshousewife.undertest.techniques.contracts.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/* BEFORE: the stock service team's own tests. They check what the team thought of, not what callers read. */
class StockServiceTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final Shelves shelves = new Shelves();
    private final StockServer service = StockServices.start(shelves);

    @AfterEach
    void stop() {
        service.close();
    }

    private HttpResponse<String> get(final String path) throws IOException, InterruptedException {
        try (HttpClient http = HttpClient.newHttpClient()) {
            final URI uri = URI.create("http://127.0.0.1:" + service.port() + path);
            return http.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString());
        }
    }

    @Test
    void anItemOnTheShelves() throws IOException, InterruptedException {
        shelves.put("KETTLE-01", 3);
        final HttpResponse<String> answer = get("/stock/KETTLE-01");
        final JsonNode item = JSON.readTree(answer.body());
        assertAll(
                () -> assertThat(answer.statusCode()).isEqualTo(200),
                () -> assertThat(item.path("sku").asString()).isEqualTo("KETTLE-01"));
    }

    @Test
    void anItemNobodyStocks() throws IOException, InterruptedException {
        assertThat(get("/stock/TOASTER-99").statusCode()).isEqualTo(404);
    }
}
