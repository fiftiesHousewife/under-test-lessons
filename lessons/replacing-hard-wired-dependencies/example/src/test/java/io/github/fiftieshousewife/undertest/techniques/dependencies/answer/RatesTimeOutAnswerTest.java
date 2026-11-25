package io.github.fiftieshousewife.undertest.techniques.dependencies.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.fiftieshousewife.undertest.techniques.dependencies.Invoices;
import io.github.fiftieshousewife.undertest.techniques.dependencies.rates.ExchangeRates;
import java.util.List;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: the stand-in plays the bank's service timing out, which the real service can't be made to do
 * on demand, and the test checks the invoice passes that error on rather than charging a wrong amount. Today's invoice
 * never asks the stand-in, so with {@code -Pcode=answer} it fails with the live service's error instead.
 */
class RatesTimeOutAnswerTest {

    @Test
    void anInvoiceIsNotChargedWhenTheRatesTimeOut() {
        final ExchangeRates rates = mock(ExchangeRates.class);
        when(rates.eurosPerPound()).thenThrow(new IllegalStateException("the bank's service timed out"));

        final Throwable error = catchThrowable(() -> Invoices.pricedWith(rates).totalInEuroCents(List.of(1000)));

        assertThat(error).extracting(Throwable::getMessage).isEqualTo("the bank's service timed out");
    }
}
