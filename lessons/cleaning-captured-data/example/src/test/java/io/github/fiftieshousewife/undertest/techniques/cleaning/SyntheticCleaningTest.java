package io.github.fiftieshousewife.undertest.techniques.cleaning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.fiftieshousewife.undertest.techniques.cleaning.fixed.SyntheticCleaning;
import org.junit.jupiter.api.Test;

/** Seeded synthetic values keep the reports working too, and the same seed gives the same values on every run. */
class SyntheticCleaningTest {

    private static final long SEED = 42L;

    @Test
    void syntheticValuesKeepTheReportsAndRepeat() {
        final Capture captured = Captures.capturedDay();
        final Capture cleaned = new SyntheticCleaning(SEED).clean(captured);
        final PersonalDataDetector detector = new PersonalDataDetector(Captures.customerNames());
        assertAll(
                () -> assertThat(DayReport.ordersPerCustomer(cleaned)).as("orders per customer")
                        .isEqualTo(DayReport.ordersPerCustomer(captured)),
                () -> assertThat(DayReport.unsettledAccounts(cleaned)).as("unsettled accounts")
                        .isEqualTo(DayReport.unsettledAccounts(captured)),
                () -> assertThat(detector.findings(cleaned)).as("personal data left").isEmpty(),
                () -> assertThat(new SyntheticCleaning(SEED).clean(captured)).as("a second run").isEqualTo(cleaned));
    }
}
