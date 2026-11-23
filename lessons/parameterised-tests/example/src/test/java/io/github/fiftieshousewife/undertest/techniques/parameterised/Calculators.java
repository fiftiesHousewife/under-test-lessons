package io.github.fiftieshousewife.undertest.techniques.parameterised;

import java.util.Set;

/**
 * The calculator the tests run against: the nearest-penny one, or the one in use today with -Pcode=today, and with
 * -Pcode=answer, which runs the exercise's answer beside today's tests.
 */
public final class Calculators {

    private Calculators() {
    }

    public static VatCalculator current() {
        final boolean today = Set.of("today", "answer").contains(System.getProperty("code"));
        return today ? new RoundingDownVat() : new NearestPennyVat();
    }
}
