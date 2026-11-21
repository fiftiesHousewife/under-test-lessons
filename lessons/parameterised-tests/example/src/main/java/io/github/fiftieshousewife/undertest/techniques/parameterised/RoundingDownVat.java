package io.github.fiftieshousewife.undertest.techniques.parameterised;

/**
 * The calculator in use today. 20% is a fifth, so it divides by five, but integer division rounds down: 3 pence net
 * gives 0.6 pence VAT, which should round to 1 penny, and this gives 0. Amounts that divide by five come out right.
 */
public class RoundingDownVat implements VatCalculator {

    @Override
    public int vatPence(final int netPence) {
        return netPence / 5;
    }
}
