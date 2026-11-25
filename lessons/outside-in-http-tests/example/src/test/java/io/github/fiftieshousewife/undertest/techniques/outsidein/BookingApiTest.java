package io.github.fiftieshousewife.undertest.techniques.outsidein;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/* AFTER: REST Assured tests the running service from outside, through its API only, as a caller sees it. */
class BookingApiTest {

    private final BookingServer service = Services.start();

    @AfterEach
    void stop() {
        service.close();
    }

    @Test
    void bookingTwoNightsCreatesABooking() {
        given().port(service.port()).contentType("application/json").body("{\"room\": \"101\", \"nights\": 2}")
                .when().post("/bookings")
                .then().statusCode(201)
                .header("Location", startsWith("/bookings/"))
                .body("pricePence", equalTo(18_000));
    }

    @Test
    void bookingNoNightsIsRefused() {
        given().port(service.port()).contentType("application/json").body("{\"room\": \"101\", \"nights\": 0}")
                .when().post("/bookings")
                .then().statusCode(400)
                .body("error", equalTo("nights must be at least 1"));
    }
}
