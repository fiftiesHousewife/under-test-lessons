package io.github.fiftieshousewife.undertest.techniques.dependencies.rates;

import java.math.BigDecimal;

/**
 * The live rates, from the bank's rate service. In production this is a network call with an account and a password;
 * this example stands in for it with the error a test machine gets: no network, no account.
 */
public enum LiveExchangeRates implements ExchangeRates {
    INSTANCE;

    @Override
    public BigDecimal eurosPerPound() {
        throw new IllegalStateException("cannot reach the bank's exchange-rate service");
    }
}
