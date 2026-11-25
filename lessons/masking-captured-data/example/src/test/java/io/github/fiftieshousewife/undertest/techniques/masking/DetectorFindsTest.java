package io.github.fiftieshousewife.undertest.techniques.masking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** The detector is only worth trusting if it finds what is in the capture before masking. */
class DetectorFindsTest {

    @Test
    void detectorFindsEveryKindOfPersonalDataBeforeMasking() {
        final PersonalDataDetector detector = new PersonalDataDetector(Captures.customerNames());
        assertThat(detector.findings(Captures.capturedDay())).as("findings before masking").contains(
                "orders.csv row 1, customer: a customer's name",
                "orders.csv row 1, email: an email address",
                "orders.csv row 1, account: a card or account number",
                "orders.csv row 4, note: an email address",
                "orders.csv row 7, note: a customer's name",
                "payments.csv row 1, card: a card or account number")
                .doesNotContain("orders.csv row 2, note: a card or account number");
    }

    @Test
    void detectorFindsEachKnownShapeOfPersonalNumber() {
        assertAll(
                () -> assertThat(findingsIn("GB82WEST12345698765432")).contains("note: an IBAN",
                        "note: a card or account number"),
                () -> assertThat(findingsIn("paid from 20-00-00")).containsExactly("note: a sort code"),
                () -> assertThat(findingsIn("call 07700 900123")).containsExactly("note: a phone number"),
                () -> assertThat(findingsIn("call +44 20 7946 0958")).containsExactly("note: a phone number"),
                () -> assertThat(findingsIn("call (415) 555-0132")).containsExactly("note: a US phone number"),
                () -> assertThat(findingsIn("call +1 415 555-0132")).containsExactly("note: a US phone number"),
                () -> assertThat(findingsIn("SSN 123-45-6789")).containsExactly("note: a Social Security number"),
                () -> assertThat(findingsIn("ships to 94105-1234")).containsExactly("note: a ZIP code"),
                () -> assertThat(findingsIn("lives at SW1A 1AA")).containsExactly("note: a postcode"),
                () -> assertThat(findingsIn("NI AB 12 34 56 C")).containsExactly("note: a National Insurance number"));
    }

    @Test
    void detectorPassesSubstitutesTestEmailsAndOrdinaryValues() {
        assertThat(findingsIn("Customer JBMFDCAAIFKJDALG, ab@example.invalid, TEST12345678, A-1001, 2026-03-14, 1250"))
                .isEmpty();
    }

    private static List<String> findingsIn(final String note) {
        final CapturedFile notes = new CapturedFile("notes.csv", List.of("note"), List.of(Map.of("note", note)));
        final CapturedFile none = new CapturedFile("payments.csv", List.of("card"), List.of());
        return new PersonalDataDetector(List.of()).findings(new Capture(notes, none)).stream()
                .map(finding -> finding.replace("notes.csv row 1, ", "")).toList();
    }
}
