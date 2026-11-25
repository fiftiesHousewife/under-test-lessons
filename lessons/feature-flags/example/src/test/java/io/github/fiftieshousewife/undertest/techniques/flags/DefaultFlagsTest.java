package io.github.fiftieshousewife.undertest.techniques.flags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/* BEFORE: tests that never set the flag. They only ever run the old rules, which is what customers get today. */
class DefaultFlagsTest {

    @Test
    void smallBasketPaysForDeliveryAndBigBasketDoesNot() {
        final Checkout checkout = Checkouts.withDefaultFlags();

        assertAll(
                () -> assertThat(checkout.shippingPence(2000)).as("a £20 basket").isEqualTo(399),
                () -> assertThat(checkout.shippingPence(6000)).as("a £60 basket").isZero());
    }
}
