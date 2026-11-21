package io.github.fiftieshousewife.undertest.techniques.differential;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** BEFORE: the rewrite's own tests: one typical weight from each row of the price list. */
class RewriteExamplesTest {

    @ParameterizedTest(name = "{0} g costs {1} pence", quoteTextArguments = false)
    @CsvSource({"50, 85", "200, 155", "400, 195", "800, 315", "1500, 435"})
    void typicalWeightCostsTheListedPrice(final int grams, final int pence) {
        assertThat(Rewrites.current().pence(grams)).isEqualTo(pence);
    }
}
