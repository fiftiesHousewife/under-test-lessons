package io.github.fiftieshousewife.undertest.techniques.mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/*
 * AFTER: the order book is a fake, a map of real orders. The only mock is the card payments, where money leaves the
 * shop, and an argument captor catches the refund the code really sent, so the test can check its amount.
 */
class CapturedRefundTest {

    private static final OrderBook ORDERS = Map.of("A-1", Shop.MUG_AND_TEAPOT)::get;

    private final CardPayments payments = mock(CardPayments.class);
    private final ArgumentCaptor<Refund> sent = ArgumentCaptor.forClass(Refund.class);

    @Test
    void refundsOnlyTheReturnedMug() {
        final int refunded = Shop.refunds(ORDERS, payments).refund("A-1", List.of("mug"));

        assertAll(
                () -> verify(payments).refund(sent.capture()),
                () -> assertThat(refunded).as("pence refunded").isEqualTo(1250),
                () -> assertThat(sent.getValue()).as("refund sent").isEqualTo(new Refund("card ending 4242", 1250)));
    }
}
