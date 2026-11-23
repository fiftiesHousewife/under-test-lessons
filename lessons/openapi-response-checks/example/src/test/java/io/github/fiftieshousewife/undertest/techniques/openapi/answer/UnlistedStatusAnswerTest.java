package io.github.fiftieshousewife.undertest.techniques.openapi.answer;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.fiftieshousewife.undertest.techniques.openapi.DocumentCheck;
import io.github.fiftieshousewife.undertest.techniques.openapi.Parcel;
import io.github.fiftieshousewife.undertest.techniques.openapi.ParcelServer;
import io.github.fiftieshousewife.undertest.techniques.openapi.Services;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * THE EXERCISE'S ANSWER: a parcel whose status is LOST, which the document's list of statuses doesn't include. A test
 * that checks a few fields would pass it; the document check refuses the answer and names the status field. Run it
 * against today's service with {@code -Pcode=answer}.
 */
class UnlistedStatusAnswerTest {

    @Test
    void aStatusTheDocumentDoesNotListIsRefused() {
        final Parcel lost = new Parcel("P-2002", "LOST", 800);
        try (ParcelServer service = new ParcelServer(List.of(lost), Services.bodies())) {
            assertThatThrownBy(() -> given().port(service.port()).filter(new DocumentCheck("openapi.yaml"))
                    .when().get("/parcels/P-2002"))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("field status: does not have a value in the enumeration");
        }
    }
}
