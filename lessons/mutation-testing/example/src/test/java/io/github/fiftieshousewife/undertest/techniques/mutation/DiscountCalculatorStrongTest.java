package io.github.fiftieshousewife.undertest.techniques.mutation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * AFTER: the same line coverage as the weak suite, but PIT kills all 13 mutants.
 * The fixes: test both sides of every boundary, assert exact values, and assert every result.
 * Several assertions in one test are wrapped in assertAll, so every failure is reported, not just the first.
 */
class DiscountCalculatorStrongTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    @Test
    void discountChangesExactlyAtEachBoundary() {
        assertAll(
                () -> assertThat(calc.discountPercent(0, false)).isZero(),        // kills `< 0` -> `<= 0`
                () -> assertThat(calc.discountPercent(49, false)).isZero(),
                () -> assertThat(calc.discountPercent(50, false)).isEqualTo(5),   // kills `>= 50` -> `> 50`
                () -> assertThat(calc.discountPercent(99, false)).isEqualTo(5),
                () -> assertThat(calc.discountPercent(100, false)).isEqualTo(10)  // kills `>= 100` -> `> 100`
        );
    }

    @Test
    void loyalCustomerGetsExactlyTwoPercentMore() {
        assertAll(
                () -> assertThat(calc.discountPercent(10, true)).isEqualTo(2),
                () -> assertThat(calc.discountPercent(100, true)).isEqualTo(12)
        );
    }

    @Test
    void negativeTotalIsRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> calc.discountPercent(-1, false));
    }

    @Test
    void finalPriceAppliesTheDiscount() {
        assertAll(
                () -> assertThat(calc.finalPrice(200, false)).isEqualTo(180),
                () -> assertThat(calc.finalPrice(200, true)).isEqualTo(176)
        );
    }
}
