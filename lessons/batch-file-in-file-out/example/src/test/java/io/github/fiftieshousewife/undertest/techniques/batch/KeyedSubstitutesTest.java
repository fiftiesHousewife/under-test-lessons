package io.github.fiftieshousewife.undertest.techniques.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;

/** A captured file is cleaned with keyed substitutes: one account number gets one substitute, in every file. */
class KeyedSubstitutesTest {

    @Test
    void anAccountGetsTheSameSubstituteInThePaymentsAndTheSettlementFile() throws GeneralSecurityException {
        final KeyedSubstitutes clean = new KeyedSubstitutes();

        final String payment = clean.line("40518812,1250", 0);
        final String settlement = clean.line("PAY,40518812,12.50", 1);

        assertAll(
                () -> assertThat(payment.split(",")[0]).as("same substitute in both files")
                        .isEqualTo(settlement.split(",")[1]),
                () -> assertThat(payment).as("cleaned payment").doesNotContain("40518812").matches("\\d{8},1250"),
                () -> assertThat(clean.account("40518813")).as("another account")
                        .isNotEqualTo(clean.account("40518812")));
    }
}
