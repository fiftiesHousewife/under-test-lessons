package io.github.fiftieshousewife.undertest.techniques.mutation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BEFORE: every line runs (100% line coverage), yet PIT kills only 5 of 13 mutants.
 * Each test shows a common weakness; the comment names the mutant that survives because of it.
 */
class DiscountCalculatorWeakTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    @Test
    void bigOrderGetsDiscount() {
        // Weak: "> 0" also passes for 5% or 12%. Survives: `total >= 100` replaced with false.
        assertTrue(calc.discountPercent(200, false) > 0);
    }

    @Test
    void mediumOrder() {
        // Weak: 70 is far from either boundary. Survives: `>= 50` changed to `> 50`.
        assertEquals(5, calc.discountPercent(70, false));
    }

    @Test
    void smallOrder() {
        assertEquals(0, calc.discountPercent(10, false));
    }

    @Test
    void loyalCustomer() {
        // Weak: no assertion at all. Survives: `+= 2` changed to `-= 2`, loyalty branch removed.
        calc.discountPercent(10, true);
    }

    @Test
    void negativeTotalRejected() {
        // Weak: -1 tests rejection but never that 0 is accepted. Survives: `< 0` changed to `<= 0`.
        assertThrows(IllegalArgumentException.class, () -> calc.discountPercent(-1, false));
    }

    @Test
    void finalPrice() {
        // Weak: an int is never null. Survives: every arithmetic mutant on the price line.
        assertNotNull(calc.finalPrice(200, false));
    }
}
