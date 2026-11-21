package io.github.fiftieshousewife.undertest.techniques.parameterised;

/** Rounds VAT to the nearest penny: a fifth of the net amount, plus a half, rounded down. */
public class NearestPennyVat implements VatCalculator {

    @Override
    public int vatPence(final int netPence) {
        return Math.floorDiv(netPence * 2 + 5, 10);
    }
}
