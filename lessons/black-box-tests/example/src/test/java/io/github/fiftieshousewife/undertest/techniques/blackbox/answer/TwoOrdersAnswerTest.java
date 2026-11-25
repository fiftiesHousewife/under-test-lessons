package io.github.fiftieshousewife.undertest.techniques.blackbox.answer;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.fiftieshousewife.undertest.techniques.blackbox.Settings;
import io.github.fiftieshousewife.undertest.techniques.blackbox.ShopApplication;
import io.github.fiftieshousewife.undertest.techniques.blackbox.fixed.OrderService;
import io.github.fiftieshousewife.undertest.techniques.blackbox.today.TodaysOrderService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/*
 * THE EXERCISE'S ANSWER: two customers order one after the other. Each gets its own order id, the warehouse is asked
 * twice, and both orders are kept. Run it against today's order service with {@code -Pcode=answer}.
 */
class TwoOrdersAnswerTest {

    @TempDir
    Path folder;

    @Test
    void twoOrdersInARowAreBothKept() throws IOException, SQLException {
        final WireMockServer warehouse = new WireMockServer(options().dynamicPort());
        warehouse.start();
        warehouse.stubFor(post(urlEqualTo("/reservations"))
                .willReturn(okJson("{\"reservationId\": \"W-881\"}").withStatus(201)));
        final String database = "jdbc:h2:" + folder.resolve("shop").toString().replace('\\', '/');
        final Path settings = Files.write(folder.resolve("order-service.properties"),
                List.of("port=0", "database.url=" + database, "warehouse.url=" + warehouse.baseUrl()));
        try (ShopApplication service = start(Settings.load(settings))) {
            for (final String customer : List.of("C-42", "C-43")) {
                given().port(service.port()).contentType("application/json")
                        .body("{\"customer\": \"" + customer + "\", \"sku\": \"PEN-01\", \"quantity\": 1}")
                        .when().post("/orders").then().statusCode(201);
            }
        } finally {
            warehouse.stop();
        }
        warehouse.verify(2, postRequestedFor(urlEqualTo("/reservations")));
        assertThat(ordersKept(database)).as("orders kept").containsExactly("O-1001 C-42", "O-1002 C-43");
    }

    private static ShopApplication start(final Settings settings) {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? TodaysOrderService.start(settings) : OrderService.start(settings);
    }

    private static List<String> ordersKept(final String database) throws SQLException {
        final List<String> orders = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(database);
             PreparedStatement query = connection.prepareStatement("SELECT order_id, customer FROM orders ORDER BY 1");
             ResultSet result = query.executeQuery()) {
            while (result.next()) {
                orders.add(result.getString("order_id") + " " + result.getString("customer"));
            }
        }
        return orders;
    }
}
