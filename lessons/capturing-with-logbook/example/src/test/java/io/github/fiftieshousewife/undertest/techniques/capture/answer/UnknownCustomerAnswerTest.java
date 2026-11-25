package io.github.fiftieshousewife.undertest.techniques.capture.answer;

import io.github.fiftieshousewife.undertest.techniques.capture.OldService;
import java.io.IOException;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.QuietReporter;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: a second approved capture, for a caller who asks for a customer nobody has. The service
 * answers 404 without calling the loyalty provider, and the caller's token must still be hidden. Run it with today's
 * capture with {@code -Pcode=answer}.
 */
class UnknownCustomerAnswerTest {

    @Test
    void approvesTheCaptureForACustomerNobodyHas() throws IOException, InterruptedException {
        final String capture = OldService.captureOf("C-9999");
        try {
            Approvals.verify(capture, new Options().withReporter(new QuietReporter()));
        } catch (Error difference) {
            // ApprovalTests' own message lists full file paths; the build's approval summary shows the lines instead
            throw new AssertionError("the capture for a customer nobody has differs from its approved file",
                    difference);
        }
    }
}
