package io.github.fiftieshousewife.undertest.techniques.cleaning;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** The detector is only worth trusting if it finds what is in the capture before cleaning. */
class DetectorFindsTest {

    @Test
    void detectorFindsEveryKindOfPersonalDataBeforeCleaning() {
        final PersonalDataDetector detector = new PersonalDataDetector(Captures.customerNames());
        assertThat(detector.findings(Captures.capturedDay())).as("findings before cleaning").contains(
                "orders.csv row 1, customer: a customer's name",
                "orders.csv row 1, email: an email address",
                "orders.csv row 1, account: a card or account number",
                "orders.csv row 4, note: an email address",
                "orders.csv row 7, note: a customer's name",
                "payments.csv row 1, card: a card or account number")
                .doesNotContain("orders.csv row 2, note: a card or account number");
    }
}
