package io.github.fiftieshousewife.undertest.techniques.changedcode;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * BEFORE: the change's own test, run with -Pcode=today. It runs every new line, so the changed code's line coverage
 * is 100%, but it checks nothing exact: no quantity at a boundary, and no exact price.
 */
class BulkDiscountWeakTest {

    private final BulkDiscount discount = new BulkDiscount();

    @Test
    void smallLinePaysSomething() {
        assertThat(discount.lineTotal(200, 3)).isPositive();
    }

    @Test
    void bulkLineIsCheaper() {
        assertThat(discount.lineTotal(200, 20)).isLessThan(200 * 20);
    }

    @Test
    void largeBulkLineIsCheaper() {
        assertThat(discount.lineTotal(200, 60)).isLessThan(200 * 60);
    }
}
