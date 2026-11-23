package io.github.fiftieshousewife.undertest.techniques.changedcode.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.fiftieshousewife.undertest.techniques.changedcode.BulkDiscount;
import org.junit.jupiter.api.Test;

/**
 * THE EXERCISE'S ANSWER: one test, added to the change's weak test, that gets the gate to pass. Exact line totals at
 * £2 an item, on both sides of each boundary, catch all 5 planted bugs the weak test missed: the two boundaries
 * moved by one, the first condition reversed, and the two sums with an operator swapped. Run it with
 * {@code -Pcode=answer}.
 */
class BulkDiscountAnswerTest {

    private final BulkDiscount discount = new BulkDiscount();

    @Test
    void exactTotalsEachSideOfEachBoundary() {
        assertAll(
                () -> assertThat(discount.lineTotal(200, 9)).as("9 items, no discount").isEqualTo(1800),
                () -> assertThat(discount.lineTotal(200, 10)).as("10 items, 5% off").isEqualTo(1900),
                () -> assertThat(discount.lineTotal(200, 49)).as("49 items, 5% off").isEqualTo(9310),
                () -> assertThat(discount.lineTotal(200, 50)).as("50 items, 10% off").isEqualTo(9000));
    }
}
