package io.github.fiftieshousewife.undertest.techniques.cleaning;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;

/** AFTER: a detector scans every field of the cleaned capture, including fields nobody wrote a rule for. */
class NothingPersonalLeftTest {

    @Test
    void cleanedCaptureHasNothingThatLooksPersonal() throws GeneralSecurityException {
        final Capture cleaned = Captures.cleaning().clean(Captures.capturedDay());
        final PersonalDataDetector detector = new PersonalDataDetector(Captures.customerNames());
        assertThat(detector.findings(cleaned)).as("personal data left after cleaning").isEmpty();
    }
}
