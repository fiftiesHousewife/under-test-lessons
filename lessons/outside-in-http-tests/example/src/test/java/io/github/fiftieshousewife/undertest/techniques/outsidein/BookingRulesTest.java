package io.github.fiftieshousewife.undertest.techniques.outsidein;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/** BEFORE: tests of the classes inside the service. They pass, and never see what a caller gets back over HTTP. */
class BookingRulesTest {

    @Test
    void twoNightsCostTwiceTheNightlyRate() {
        assertAll(
                () -> assertThat(BookingRules.pricePence(2)).isEqualTo(18_000),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> BookingRules.pricePence(0))
                        .withMessage("nights must be at least 1"));
    }
}
