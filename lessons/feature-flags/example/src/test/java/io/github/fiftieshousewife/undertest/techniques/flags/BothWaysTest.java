package io.github.fiftieshousewife.undertest.techniques.flags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/* AFTER: the same checks with the flag off and on. Until the old rules are removed, both ways must agree. */
class BothWaysTest {

    @ParameterizedTest(name = "new shipping rules switched on: {0}")
    @ValueSource(booleans = {false, true})
    void smallBasketPaysForDeliveryAndBigBasketDoesNot(final boolean newShipping) {
        final Checkout checkout = Checkouts.withNewShipping(newShipping);

        assertAll(
                () -> assertThat(checkout.shippingCents(2000)).as("a €20 basket").isEqualTo(399),
                () -> assertThat(checkout.shippingCents(6000)).as("a €60 basket").isZero());
    }
}
