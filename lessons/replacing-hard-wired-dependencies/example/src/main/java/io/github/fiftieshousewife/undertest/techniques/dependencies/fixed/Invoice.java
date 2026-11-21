package io.github.fiftieshousewife.undertest.techniques.dependencies.fixed;

import io.github.fiftieshousewife.undertest.techniques.dependencies.PricedInvoice;
import io.github.fiftieshousewife.undertest.techniques.dependencies.rates.ExchangeRates;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** FIXED: the rates are passed in. Production passes the live rates; a test passes a stand-in. */
public final class Invoice implements PricedInvoice {

    private final ExchangeRates rates;

    public Invoice(final ExchangeRates rates) {
        this.rates = rates;
    }

    @Override
    public int totalInEuroCents(final List<Integer> linesInPence) {
        final int pence = linesInPence.stream().mapToInt(Integer::intValue).sum();
        final BigDecimal rate = rates.eurosPerPound();
        return BigDecimal.valueOf(pence).multiply(rate).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }
}
