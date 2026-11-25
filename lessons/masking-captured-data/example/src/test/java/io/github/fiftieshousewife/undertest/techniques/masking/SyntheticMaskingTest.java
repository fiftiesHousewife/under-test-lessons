package io.github.fiftieshousewife.undertest.techniques.masking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.fiftieshousewife.undertest.techniques.masking.fixed.SyntheticMasking;
import org.junit.jupiter.api.Test;

/* Seeded synthetic values keep the reports working too, and the same seed gives the same values on every run. */
class SyntheticMaskingTest {

    private static final long SEED = 42L;

    @Test
    void syntheticValuesKeepTheReportsAndRepeat() {
        final Capture captured = Captures.capturedDay();
        final Capture masked = new SyntheticMasking(SEED).mask(captured);
        final PersonalDataDetector detector = new PersonalDataDetector(Captures.customerNames());
        assertAll(
                () -> assertThat(DayReport.ordersPerCustomer(masked)).as("orders per customer")
                        .isEqualTo(DayReport.ordersPerCustomer(captured)),
                () -> assertThat(DayReport.unsettledAccounts(masked)).as("unsettled accounts")
                        .isEqualTo(DayReport.unsettledAccounts(captured)),
                () -> assertThat(detector.findings(masked)).as("personal data left").isEmpty(),
                () -> assertThat(new SyntheticMasking(SEED).mask(captured)).as("a second run").isEqualTo(masked));
    }
}
