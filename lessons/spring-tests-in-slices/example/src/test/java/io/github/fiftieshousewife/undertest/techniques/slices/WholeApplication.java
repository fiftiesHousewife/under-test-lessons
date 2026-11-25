package io.github.fiftieshousewife.undertest.techniques.slices;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

/*
 * The whole application, started on a free port: every bean, every setting, the database and the courier. The
 * courier is a WireMock stand-in, and the tests add the database's rows themselves.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class WholeApplication {

    /* Started once for every test class: Spring keeps the started application for the next class, and its setting. */
    static final WireMockServer COURIER = startCourier();

    @Autowired
    private DeliveryRepository repository;

    @LocalServerPort
    private int port;

    private static WireMockServer startCourier() {
        final WireMockServer courier = new WireMockServer(wireMockConfig().dynamicPort());
        courier.start();
        return courier;
    }

    @DynamicPropertySource
    static void courierSettings(final DynamicPropertyRegistry settings) {
        settings.add("courier.base-url", COURIER::baseUrl);
    }

    @BeforeEach
    void deliveriesAndTracking() {
        repository.saveAll(ExampleDeliveries.all());
        COURIER.stubFor(get("/tracking/D-1001").willReturn(okJson(
                "{\"reference\": \"D-1001\", \"status\": \"At the depot\"}")));
        COURIER.stubFor(get("/tracking/D-1002").willReturn(okJson(
                "{\"reference\": \"D-1002\", \"status\": \"Out for delivery\"}")));
    }

    @AfterEach
    void emptyTheTable() {
        repository.deleteAll();
        COURIER.resetAll();
    }

    /* The body of the service's answer to GET on this path. */
    String bodyOf(final String path) {
        return RestClient.create("http://localhost:" + port).get().uri(path).retrieve().body(String.class);
    }
}
