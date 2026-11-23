package io.github.fiftieshousewife.undertest.techniques.cleaning.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.cleaning.Capture;
import io.github.fiftieshousewife.undertest.techniques.cleaning.CapturedFile;
import io.github.fiftieshousewife.undertest.techniques.cleaning.Captures;
import io.github.fiftieshousewife.undertest.techniques.cleaning.PersonalDataDetector;
import java.security.GeneralSecurityException;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * THE EXERCISE'S ANSWER: tomorrow's capture may hold what today's doesn't. Here one order's note has a phone number,
 * a shape the captured day never shows, and the cleaned order must still have nothing that looks personal. Run it
 * against today's cleaning with {@code -Pcode=answer}.
 */
class PhoneInANoteAnswerTest {

    @Test
    void aPhoneNumberInANoteIsCleanedToo() throws GeneralSecurityException {
        final CapturedFile orders = CapturedFile.parse("orders.csv", List.of(
                "order,customer,email,account,pence,note",
                "O-2001,Sam Patel,sam.patel@inbox.test,40551234,700,Call 07700 900123 before delivery"));
        final CapturedFile payments = CapturedFile.parse("payments.csv", List.of("payment,account,card,pence"));
        final Capture cleaned = Captures.cleaning().clean(new Capture(orders, payments));
        assertThat(new PersonalDataDetector(List.of("Sam Patel")).findings(cleaned))
                .as("personal data left after cleaning an order with a phone number").isEmpty();
    }
}
