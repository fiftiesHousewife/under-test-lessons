package io.github.fiftieshousewife.undertest.techniques.capture;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.QuietReporter;
import org.junit.jupiter.api.Test;

/*
 * AFTER: reads what the capture wrote. One test fails if a secret is in it; the other keeps the whole captured
 * exchange as an approved file next to this test, and fails if a capture ever differs from it, line for line.
 */
class ApprovedCaptureTest {

    private static final Map<String, String> SECRETS = Map.of(
            "the caller's token", OldService.CALLER_TOKEN,
            "the loyalty provider's key", OldService.LOYALTY_KEY,
            "the customer's email", OldService.EMAIL);

    @Test
    void writesNoSecrets() throws IOException, InterruptedException {
        try (OldService service = new OldService()) {
            service.customer("C-1001");
            final String capture = service.captured();
            assertThat(SECRETS.keySet().stream().filter(name -> capture.contains(SECRETS.get(name))).sorted())
                    .as("secrets in the capture").isEmpty();
        }
    }

    @Test
    void matchesTheApprovedCapture() throws IOException, InterruptedException {
        try (OldService service = new OldService()) {
            service.customer("C-1001");
            verifyApproved(service.captured());
        }
    }

    /*
     * Compares the capture with its approved file. ApprovalTests' own message lists full file paths, so a failure says
     * what failed in one line, and the build's approval summary shows the lines that differ.
     */
    private static void verifyApproved(final String capture) {
        try {
            Approvals.verify(capture, new Options().withReporter(new QuietReporter()));
        } catch (Error difference) {
            throw new AssertionError("the capture differs from its approved file", difference);
        }
    }
}
