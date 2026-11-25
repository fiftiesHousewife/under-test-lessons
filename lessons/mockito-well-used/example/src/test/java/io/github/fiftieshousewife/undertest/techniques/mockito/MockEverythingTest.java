package io.github.fiftieshousewife.undertest.techniques.mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;

/*
 * BEFORE: everything around the refunds is a mock, and the test checks the calls the code makes. That repeats the
 * code, and any refund will do, so it passes while the card gets the whole order back.
 */
class MockEverythingTest {

    private final OrderBook orders = mock(OrderBook.class);
    private final CardPayments payments = mock(CardPayments.class);

    @Test
    void refundsTheReturnedMug() {
        when(orders.find("A-1")).thenReturn(Shop.MUG_AND_TEAPOT);

        Shop.refunds(orders, payments).refund("A-1", List.of("mug"));

        verify(orders).find("A-1");
        verify(payments).refund(any(Refund.class));
    }
}
