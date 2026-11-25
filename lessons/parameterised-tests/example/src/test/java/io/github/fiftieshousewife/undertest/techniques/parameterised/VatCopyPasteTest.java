package io.github.fiftieshousewife.undertest.techniques.parameterised;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/*
 * BEFORE: one test per case, copied and pasted. Each new case costs a whole method, so there are few, and they are
 * all round numbers: every amount here divides by five, so rounding never comes into it.
 */
class VatCopyPasteTest {

    private final VatCalculator vat = Calculators.current();

    @Test
    void noVatOnNothing() {
        assertThat(vat.vatPence(0)).isZero();
    }

    @Test
    void vatOnOnePound() {
        assertThat(vat.vatPence(100)).isEqualTo(20);
    }

    @Test
    void vatOnTenPounds() {
        assertThat(vat.vatPence(1000)).isEqualTo(200);
    }
}
