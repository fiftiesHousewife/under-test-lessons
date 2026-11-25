package io.github.fiftieshousewife.undertest.techniques.masking;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;

/* AFTER: a detector scans every field of the masked capture, including fields nobody wrote a rule for. */
class NothingPersonalLeftTest {

    @Test
    void maskedCaptureHasNothingThatLooksPersonal() throws GeneralSecurityException {
        final Capture masked = Captures.masking().mask(Captures.capturedDay());
        final PersonalDataDetector detector = new PersonalDataDetector(Captures.customerNames());
        assertThat(detector.findings(masked)).as("personal data left after masking").isEmpty();
    }
}
