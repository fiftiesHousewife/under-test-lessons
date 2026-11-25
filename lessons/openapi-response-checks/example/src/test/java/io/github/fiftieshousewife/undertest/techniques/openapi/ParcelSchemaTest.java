package io.github.fiftieshousewife.undertest.techniques.openapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** AFTER: the same requests and checks, and every answer is also checked against the OpenAPI schema, openapi.yaml. */
class ParcelSchemaTest {

    private static final SchemaCheck THE_SCHEMA = new SchemaCheck("openapi.yaml");

    private final ParcelServer service = Services.start();

    @AfterEach
    void stop() {
        service.close();
    }

    private RequestSpecification request() {
        return given().port(service.port()).filter(THE_SCHEMA);
    }

    @Test
    void aParcelOnItsWay() {
        request()
                .when().get("/parcels/P-1001")
                .then().statusCode(200)
                .body("id", equalTo("P-1001"))
                .body("status", equalTo("OUT_FOR_DELIVERY"));
    }

    @Test
    void aParcelNobodySent() {
        request()
                .when().get("/parcels/P-0000")
                .then().statusCode(404);
    }
}
