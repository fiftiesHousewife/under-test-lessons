package io.github.fiftieshousewife.undertest.techniques.changedcode;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/*
 * AFTER: the stronger test for the same change. Exact percentages on both sides of each boundary, and exact prices,
 * so every planted bug in the changed class makes a test fail.
 */
class BulkDiscountStrongTest {

    private final BulkDiscount discount = new BulkDiscount();

    @Test
    void percentOffEachSideOfEachBoundary() {
        assertAll(
                () -> assertThat(discount.percentOff(9)).as("9 items").isZero(),
                () -> assertThat(discount.percentOff(10)).as("10 items").isEqualTo(5),
                () -> assertThat(discount.percentOff(49)).as("49 items").isEqualTo(5),
                () -> assertThat(discount.percentOff(50)).as("50 items").isEqualTo(10));
    }

    @Test
    void exactLineTotals() {
        assertAll(
                () -> assertThat(discount.lineTotal(200, 3)).as("3 at £2, no discount").isEqualTo(600),
                () -> assertThat(discount.lineTotal(199, 10)).as("10 at £1.99, 5% off rounded down").isEqualTo(1891),
                () -> assertThat(discount.lineTotal(200, 50)).as("50 at £2, 10% off").isEqualTo(9000));
    }
}
