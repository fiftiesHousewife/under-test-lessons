package io.github.fiftieshousewife.undertest.techniques.metamorphic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/*
 * BEFORE: the example tests, baskets whose totals a developer worked out by hand: an empty one, an everyday one and a
 * big one. Each needs its right answer, so there are none near 10,000 yen, where the right answer isn't known.
 */
class BasketExamplesTest {

    private final BasketPricing pricing = Pricings.current();

    @Test
    void anEmptyBasketCostsNothing() {
        assertThat(pricing.totalYen(Basket.of())).isZero();
    }

    @Test
    void anEverydayBasketCostsItsPrices() {
        final Basket basket = Basket.of(new Line("rice", 2_980, 2), new Line("green tea", 1_280, 1));
        assertThat(pricing.totalYen(basket)).isEqualTo(7_240);
    }

    @Test
    void aBigBasketGetsTwentyPercentOff() {
        final Basket basket = Basket.of(new Line("rice", 2_980, 5));
        assertThat(pricing.totalYen(basket)).isEqualTo(11_920);
    }
}
