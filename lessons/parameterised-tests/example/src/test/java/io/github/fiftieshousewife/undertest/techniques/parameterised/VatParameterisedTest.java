package io.github.fiftieshousewife.undertest.techniques.parameterised;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/*
 * AFTER: one parameterised test, with the cases as a table. A new case is one more row, so it is cheap to cover every
 * way the rounding can go: a fifth of the net amount ends in .0, .2, .4, .6 or .8 of a penny. Each row runs, and is
 * reported, as its own test.
 */
class VatParameterisedTest {

    private final VatCalculator vat = Calculators.current();

    @ParameterizedTest(name = "{0} pence net gives {1} pence VAT", quoteTextArguments = false)
    @CsvSource({
        "0,    0",
        "1,    0",
        "2,    0",
        "3,    1",
        "4,    1",
        "5,    1",
        "998,  200",
        "1000, 200",
    })
    void roundsVatToTheNearestPenny(final int netPence, final int expectedVat) {
        assertThat(vat.vatPence(netPence)).isEqualTo(expectedVat);
    }
}
