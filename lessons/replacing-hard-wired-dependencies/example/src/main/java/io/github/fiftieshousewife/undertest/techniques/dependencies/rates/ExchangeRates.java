package io.github.fiftieshousewife.undertest.techniques.dependencies.rates;

import java.math.BigDecimal;

/** Where exchange rates come from. */
public interface ExchangeRates {

    /** How many euros one pound buys today. */
    BigDecimal eurosPerPound();
}
