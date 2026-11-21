package io.github.fiftieshousewife.undertest.techniques.parameterised;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/** Pins down the bug the table found in the calculator in use today: it rounds down, not to the nearest penny. */
class RoundingDownVatTest {

    @Test
    void roundsDownWhereItShouldRoundUp() {
        assertAll(
                () -> assertThat(new RoundingDownVat().vatPence(3)).isZero(),
                () -> assertThat(new NearestPennyVat().vatPence(3)).isEqualTo(1));
    }
}
