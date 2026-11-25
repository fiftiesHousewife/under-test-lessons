package io.github.fiftieshousewife.undertest.techniques.extensions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/* How prices are written: VAT to the nearest penny, and a credit with its sign in front. */
class PricesTest {

    @Test
    void vatIsRoundedToTheNearestPenny() {
        assertAll(
                () -> assertThat(Prices.withVat(10_000, 20)).isEqualTo("£120.00"),
                () -> assertThat(Prices.withVat(1_999, 20)).isEqualTo("£23.99"),
                () -> assertThat(Prices.withVat(1_997, 20)).isEqualTo("£23.96"));
    }

    @Test
    void aCreditIsWrittenWithItsSignInFront() {
        assertAll(
                () -> assertThat(Prices.pounds(-5)).isEqualTo("-£0.05"),
                () -> assertThat(Prices.pounds(-12_050)).isEqualTo("-£120.50"));
    }
}
