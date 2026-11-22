package io.github.fiftieshousewife.undertest.techniques.blackbox;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * AFTER: the order service started as it ships, from a settings file, with WireMock as the warehouse and its own
 * database in a temporary folder. The test checks only what comes out: the answer, the call, and the rows written.
 */
class OrderServiceTest {

    private final WireMockServer warehouse = new WireMockServer(options().dynamicPort());
    private ShopApplication service;
    private String database;

    @BeforeEach
    void start(@TempDir final Path folder) throws IOException {
        warehouse.start();
        database = "jdbc:h2:" + folder.resolve("shop").toString().replace('\\', '/'); // as a settings file needs
        final Path settings = Files.writeString(folder.resolve("order-service.properties"), "port=0\n"
                + "database.url=" + database + "\n" + "warehouse.url=" + warehouse.baseUrl() + "\n");
        service = Applications.start(Settings.load(settings));
    }

    @AfterEach
    void stop() {
        service.close();
        warehouse.stop();
    }

    @Test
    void anOrderIsReservedAnsweredAndKept() throws SQLException {
        warehouse.stubFor(post(urlEqualTo("/reservations"))
                .willReturn(okJson("{\"reservationId\": \"W-881\"}").withStatus(201)));
        given().port(service.port()).contentType("application/json")
                .body("{\"customer\": \"C-42\", \"sku\": \"PEN-01\", \"quantity\": 3}")
                .when().post("/orders")
                .then().statusCode(201).header("Location", "/orders/O-1001").body("orderId", equalTo("O-1001"));
        warehouse.verify(postRequestedFor(urlEqualTo("/reservations"))
                .withRequestBody(equalToJson("{\"sku\": \"PEN-01\", \"quantity\": 3}")));
        assertThat(rowsWritten()).as("rows written").isEqualTo(List.of("O-1001 C-42 W-881 PEN-01 3"));
    }

    @Test
    void anOrderWithoutStockIsRefusedAndNothingIsWritten() throws SQLException {
        warehouse.stubFor(post(urlEqualTo("/reservations")).willReturn(aResponse().withStatus(409)));
        given().port(service.port()).contentType("application/json")
                .body("{\"customer\": \"C-42\", \"sku\": \"PEN-01\", \"quantity\": 300}")
                .when().post("/orders")
                .then().statusCode(409).body("error", equalTo("not enough stock of PEN-01"));
        assertThat(rowsWritten()).as("rows written").isEmpty();
    }

    /** Every order and its line, read straight from the service's database, one line each. */
    private List<String> rowsWritten() throws SQLException {
        final List<String> rows = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(database);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT o.order_id, o.customer, o.reservation_id, l.sku, "
                     + "l.quantity FROM orders o JOIN order_lines l ON l.order_id = o.order_id")) {
            while (result.next()) {
                rows.add(String.join(" ", result.getString(1), result.getString(2), result.getString(3),
                        result.getString(4), result.getString(5)));
            }
        }
        return rows;
    }
}
