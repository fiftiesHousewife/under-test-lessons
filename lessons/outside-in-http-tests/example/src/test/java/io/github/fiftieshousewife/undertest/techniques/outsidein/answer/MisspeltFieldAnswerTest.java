package io.github.fiftieshousewife.undertest.techniques.outsidein.answer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.github.fiftieshousewife.undertest.techniques.outsidein.BookingServer;
import io.github.fiftieshousewife.undertest.techniques.outsidein.fixed.BookingHandler;
import io.github.fiftieshousewife.undertest.techniques.outsidein.today.TodaysBookingHandler;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * THE EXERCISE'S ANSWER: a caller who misspells the field, sending "night" instead of "nights", must be refused with
 * 400 Bad Request, not told 200 OK. Run it against today's service with {@code -Pcode=answer}.
 */
class MisspeltFieldAnswerTest {

    @Test
    void bookingWithAMisspeltFieldIsRefused() {
        final boolean today = Set.of("today", "answer").contains(System.getProperty("code"));
        try (BookingServer service = new BookingServer(today ? new TodaysBookingHandler() : new BookingHandler())) {
            given().port(service.port()).contentType("application/json").body("{\"room\": \"101\", \"night\": 2}")
                    .when().post("/bookings")
                    .then().statusCode(400)
                    .body("error", equalTo("nights must be at least 1"));
        }
    }
}
