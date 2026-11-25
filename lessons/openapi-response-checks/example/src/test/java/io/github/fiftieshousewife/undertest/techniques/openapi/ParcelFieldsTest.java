package io.github.fiftieshousewife.undertest.techniques.openapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/* BEFORE: checks the status code and a few known fields. It passes, and never compares answers with the schema. */
class ParcelFieldsTest {

    private final ParcelServer service = Services.start();

    @AfterEach
    void stop() {
        service.close();
    }

    @Test
    void aParcelOnItsWay() {
        given().port(service.port())
                .when().get("/parcels/P-1001")
                .then().statusCode(200)
                .body("id", equalTo("P-1001"))
                .body("status", equalTo("OUT_FOR_DELIVERY"));
    }

    @Test
    void aParcelNobodySent() {
        given().port(service.port())
                .when().get("/parcels/P-0000")
                .then().statusCode(404);
    }
}
