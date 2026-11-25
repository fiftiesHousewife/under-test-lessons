package io.github.fiftieshousewife.undertest.techniques.capture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import org.junit.jupiter.api.Test;

/*
 * BEFORE: checks that the capture recorded the exchanges: the request the service received, the call it made to the
 * loyalty provider, and both answers. It never reads what was written, so a token or an email in it goes unnoticed.
 */
class CaptureRecordsTest {

    @Test
    void recordsWhatTheServiceReceivesAndSends() throws IOException, InterruptedException {
        final String capture = OldService.captureOf("C-1001");
        assertAll(
                () -> assertThat(capture).as("what it received").contains("Request received: GET /customers/C-1001"),
                () -> assertThat(capture).as("what it sent").contains("Request sent: POST /points"),
                () -> assertThat(capture).as("the answers").contains("Response received: 200", "Response sent: 200"));
    }
}
