package io.github.fiftieshousewife.undertest.techniques.dependencies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.fiftieshousewife.undertest.techniques.dependencies.rates.ExchangeRates;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* One test for both invoices. It hands over stand-in rates, made by Mockito, fixed at 1.17 euros to the pound. */
class InvoiceTest {

    private final ExchangeRates rates = mock(ExchangeRates.class);

    @BeforeEach
    void ratesAreFixed() {
        when(rates.eurosPerPound()).thenReturn(new BigDecimal("1.17"));
    }

    @Test
    void convertsTheTotalRoundingToTheNearestCent() {
        // 1,250 pence at 1.17 is 1,462.5 cents, which rounds up
        assertThat(Invoices.pricedWith(rates).totalInEuroCents(List.of(1000, 250))).isEqualTo(1463);
    }

    @Test
    void anEmptyInvoiceCostsNothing() {
        assertThat(Invoices.pricedWith(rates).totalInEuroCents(List.of())).isZero();
    }
}
