package io.github.fiftieshousewife.undertest.techniques.tags;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/* Quick: plain code, no waiting. */
class DeliveryChargeTest {

    @Test
    void chargesOnABasketJustUnderFiftyPounds() {
        assertThat(DeliveryCharge.forBasket(4_999)).isEqualTo(499);
    }

    @Test
    void isFreeOnABasketOfExactlyFiftyPounds() {
        assertThat(DeliveryCharge.forBasket(5_000)).isZero();
    }
}
