package io.github.fiftieshousewife.undertest.techniques.parameterised;

/** The calculator the tests run against: the nearest-penny one, or the one in use today with -Pvat=current. */
final class Calculators {

    private Calculators() {
    }

    static VatCalculator current() {
        return "current".equals(System.getProperty("vat")) ? new RoundingDownVat() : new NearestPennyVat();
    }
}
