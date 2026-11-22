package io.github.fiftieshousewife.undertest.techniques.parameterised;

/** The calculator the tests run against: the nearest-penny one, or the one in use today with -Pcode=today. */
final class Calculators {

    private Calculators() {
    }

    static VatCalculator current() {
        return "today".equals(System.getProperty("code")) ? new RoundingDownVat() : new NearestPennyVat();
    }
}
