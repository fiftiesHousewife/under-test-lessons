package io.github.fiftieshousewife.undertest.techniques.cleaning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;

/** BEFORE: checks that each field the cleaning was told about has changed, and that no row was lost. */
class CleanedFieldsTest {

    @Test
    void namesEmailsAndAccountsAreReplaced() throws GeneralSecurityException {
        final Capture captured = Captures.capturedDay();
        final Capture cleaned = Captures.cleaning().clean(captured);
        assertAll(
                () -> assertThat(cleaned.orders().rows()).as("orders kept").hasSameSizeAs(captured.orders().rows()),
                () -> assertThat(cleaned.orders().column("customer")).as("names")
                        .doesNotContainAnyElementsOf(captured.orders().column("customer")),
                () -> assertThat(cleaned.orders().column("email")).as("email addresses")
                        .doesNotContainAnyElementsOf(captured.orders().column("email")),
                () -> assertThat(cleaned.payments().column("account")).as("account numbers")
                        .doesNotContainAnyElementsOf(captured.payments().column("account")));
    }
}
