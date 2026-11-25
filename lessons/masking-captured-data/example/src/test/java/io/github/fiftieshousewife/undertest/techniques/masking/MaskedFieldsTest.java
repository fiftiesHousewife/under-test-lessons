package io.github.fiftieshousewife.undertest.techniques.masking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;

/** BEFORE: checks that each field the masking was told about has changed, and that no row was lost. */
class MaskedFieldsTest {

    @Test
    void namesEmailsAndAccountsAreReplaced() throws GeneralSecurityException {
        final Capture captured = Captures.capturedDay();
        final Capture masked = Captures.masking().mask(captured);
        assertAll(
                () -> assertThat(masked.orders().rows()).as("orders kept").hasSameSizeAs(captured.orders().rows()),
                () -> assertThat(masked.orders().column("customer")).as("names")
                        .doesNotContainAnyElementsOf(captured.orders().column("customer")),
                () -> assertThat(masked.orders().column("email")).as("email addresses")
                        .doesNotContainAnyElementsOf(captured.orders().column("email")),
                () -> assertThat(masked.payments().column("account")).as("account numbers")
                        .doesNotContainAnyElementsOf(captured.payments().column("account")));
    }
}
