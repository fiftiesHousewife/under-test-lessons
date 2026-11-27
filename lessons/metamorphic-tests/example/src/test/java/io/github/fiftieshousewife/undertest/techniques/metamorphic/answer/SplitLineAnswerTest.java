package io.github.fiftieshousewife.undertest.techniques.metamorphic.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.metamorphic.Basket;
import io.github.fiftieshousewife.undertest.techniques.metamorphic.BasketPricing;
import io.github.fiftieshousewife.undertest.techniques.metamorphic.Baskets;
import io.github.fiftieshousewife.undertest.techniques.metamorphic.Pricings;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: one more relation. Three miso on one line, or two on one line and one on another, are the
 * same three jars, so splitting a line in two never changes the total. Run it against today's pricing with
 * -Pcode=answer: rounding each line on its own loses a yen.
 */
class SplitLineAnswerTest {

    private final BasketPricing pricing = Pricings.current();

    @Test
    void splittingALineInTwoNeverChangesTheTotal() {
        final List<String> broken = new ArrayList<>();
        for (final Basket basket : Baskets.every()) {
            broken.addAll(splitsThatChangeTheTotal(basket));
        }
        assertThat(Baskets.first(broken)).as("splitting a line changed the total %,d times", broken.size()).isEmpty();
    }

    private List<String> splitsThatChangeTheTotal(final Basket basket) {
        final int asOneLine = pricing.totalYen(basket);
        final List<String> broken = new ArrayList<>();
        for (int place = 0; place < basket.lines().size(); place++) {
            if (basket.lines().get(place).quantity() > 1) {
                final int split = pricing.totalYen(Baskets.splitAt(basket, place));
                if (split != asOneLine) {
                    broken.add("%s: ¥%,d, with a line split: ¥%,d"
                            .formatted(Baskets.describe(basket), asOneLine, split));
                }
            }
        }
        return broken;
    }
}
