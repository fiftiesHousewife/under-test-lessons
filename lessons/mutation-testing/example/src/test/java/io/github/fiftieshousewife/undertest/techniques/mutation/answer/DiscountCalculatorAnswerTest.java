package io.github.fiftieshousewife.undertest.techniques.mutation.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.fiftieshousewife.undertest.techniques.mutation.DiscountCalculator;
import org.junit.jupiter.api.Test;

/**
 * THE EXERCISE'S ANSWER: one test, added to today's weak tests, that catches the planted bug on line 25, where
 * {@code >= 5_000} becomes {@code > 5_000}. Only an order of exactly £50 shows the difference, so the test checks both
 * sides of the boundary: one penny under, and exactly £50. Run it with {@code -Pcode=answer}.
 */
class DiscountCalculatorAnswerTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    @Test
    void discountStartsAtExactlyFiftyPounds() {
        assertAll(
                () -> assertThat(calc.discountPercent(4_999, false)).isZero(),
                () -> assertThat(calc.discountPercent(5_000, false)).isEqualTo(5));
    }
}
