package io.github.fiftieshousewife.undertest.techniques.recorded.answer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.fiftieshousewife.undertest.techniques.recorded.Clients;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: one more answer the real supplier rarely gives, added by hand as a recording
 * (supplier/mappings/too-many-requests.json): 429 Too Many Requests. The client must say the supplier is unavailable,
 * not give an empty town. Run it against today's client with {@code -Pcode=answer}.
 */
class TooManyRequestsAnswerTest {

    @Test
    void tooManyRequestsSaysTheSupplierIsUnavailable() {
        final WireMockServer supplier = new WireMockServer(options().dynamicPort()
                .usingFilesUnderClasspath("supplier"));
        supplier.start();
        try {
            final Throwable error = catchThrowable(() -> Clients.at(supplier.baseUrl()).town("B1 1BB"));
            assertThat(error == null ? "no error" : error.getMessage()).isEqualTo("the postcode supplier answered 429");
        } finally {
            supplier.stop();
        }
    }
}
