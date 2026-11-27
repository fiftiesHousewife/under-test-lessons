package io.github.fiftieshousewife.undertest.techniques.metamorphic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/*
 * AFTER: metamorphic tests. Neither states what any basket should cost. Each changes every basket in a way whose
 * effect on the total is known, and checks how the two totals compare: one more item never costs less, and the order
 * of the lines never changes the total.
 */
class BasketRelationsTest {

    private final BasketPricing pricing = Pricings.current();

    @Test
    void oneMoreItemNeverCostsLess() {
        final List<String> broken = new ArrayList<>();
        for (final Basket basket : Baskets.every()) {
            for (final Line item : Baskets.ONE_OF_EACH) {
                final int before = pricing.totalYen(basket);
                final int after = pricing.totalYen(basket.plus(item));
                if (after < before) {
                    broken.add("%s: ¥%,d, with one more %s: ¥%,d"
                            .formatted(Baskets.describe(basket), before, item.product(), after));
                }
            }
        }
        assertThat(Baskets.first(broken)).as("one more item cost less %,d times, the first", broken.size()).isEmpty();
    }

    @Test
    void theOrderOfTheLinesNeverChangesTheTotal() {
        final List<String> broken = new ArrayList<>();
        for (final Basket basket : Baskets.every()) {
            final int asListed = pricing.totalYen(basket);
            final int reversed = pricing.totalYen(Baskets.reversed(basket));
            if (reversed != asListed) {
                broken.add("%s: ¥%,d, reversed: ¥%,d".formatted(Baskets.describe(basket), asListed, reversed));
            }
        }
        assertThat(Baskets.first(broken)).as("reversing changed the total %,d times", broken.size()).isEmpty();
    }
}
