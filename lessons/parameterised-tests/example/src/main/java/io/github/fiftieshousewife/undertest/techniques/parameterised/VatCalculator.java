package io.github.fiftieshousewife.undertest.techniques.parameterised;

/** Works out VAT at 20% on a net amount, in pence, rounded to the nearest penny. */
public interface VatCalculator {

    int vatPence(int netPence);
}
