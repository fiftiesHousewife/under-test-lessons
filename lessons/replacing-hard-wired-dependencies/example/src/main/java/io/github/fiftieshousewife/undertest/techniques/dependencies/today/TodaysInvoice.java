package io.github.fiftieshousewife.undertest.techniques.dependencies.today;

import io.github.fiftieshousewife.undertest.techniques.dependencies.PricedInvoice;
import io.github.fiftieshousewife.undertest.techniques.dependencies.rates.LiveExchangeRates;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** TODAY: the rate comes from a hard-wired singleton, so this code always calls the live service, even in a test. */
public final class TodaysInvoice implements PricedInvoice {

    @Override
    public int totalInEuroCents(final List<Integer> linesInPence) {
        final int pence = linesInPence.stream().mapToInt(Integer::intValue).sum();
        final BigDecimal rate = LiveExchangeRates.INSTANCE.eurosPerPound();
        return BigDecimal.valueOf(pence).multiply(rate).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }
}
