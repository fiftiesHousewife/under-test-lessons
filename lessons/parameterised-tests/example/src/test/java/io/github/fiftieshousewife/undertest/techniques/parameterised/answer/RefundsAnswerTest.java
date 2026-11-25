package io.github.fiftieshousewife.undertest.techniques.parameterised.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.parameterised.Calculators;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/*
 * THE EXERCISE'S ANSWER: refunds. A credit note's net amount is negative, and its VAT rounds to the nearest penny
 * too: 3 pence refunded gives 0.6 pence of VAT back, which is 1 penny. Three more rows, no more code.
 */
class RefundsAnswerTest {

    @ParameterizedTest(name = "a refund: {0} pence net gives {1} pence VAT", quoteTextArguments = false)
    @CsvSource({
        "-3,   -1",
        "-5,   -1",
        "-998, -200",
    })
    void refundsRoundToTheNearestPennyToo(final int netPence, final int expectedVat) {
        assertThat(Calculators.current().vatPence(netPence)).as("VAT on the refund").isEqualTo(expectedVat);
    }
}
