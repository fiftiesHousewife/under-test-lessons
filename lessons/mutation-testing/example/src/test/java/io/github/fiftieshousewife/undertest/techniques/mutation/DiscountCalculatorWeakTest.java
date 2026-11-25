package io.github.fiftieshousewife.undertest.techniques.mutation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * BEFORE: every line runs (100% line coverage), yet PIT kills only 5 of 13 mutants.
 * Each test shows a common weakness; the comment names the mutant that survives because of it.
 */
class DiscountCalculatorWeakTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    @Test
    void bigOrderGetsDiscount() {
        // Weak: "> 0" also passes for 5% or 12%. Survives: `total >= 10_000` replaced with false.
        assertTrue(calc.discountPercent(20_000, false) > 0);
    }

    @Test
    void mediumOrder() {
        // Weak: £70 is far from either boundary. Survives: `>= 5_000` changed to `> 5_000`.
        assertEquals(5, calc.discountPercent(7_000, false));
    }

    @Test
    void smallOrder() {
        assertEquals(0, calc.discountPercent(1_000, false));
    }

    @Test
    void loyalCustomer() {
        // Weak: no assertion at all. Survives: `+= 2` changed to `-= 2`, loyalty branch removed.
        calc.discountPercent(1_000, true);
    }

    @Test
    void negativeTotalRejected() {
        // Weak: -1 tests rejection but never that 0 is accepted. Survives: `< 0` changed to `<= 0`.
        assertThrows(IllegalArgumentException.class, () -> calc.discountPercent(-1, false));
    }

    @Test
    void finalPrice() {
        // Weak: an int is never null. Survives: every arithmetic mutant on the price line.
        assertNotNull(calc.finalPrice(20_000, false));
    }
}
