package io.github.fiftieshousewife.undertest.techniques.outsidein;

/** The hotel's rules: a room costs £90 a night, and a booking is for at least one night. */
public final class BookingRules {

    public static final int PENCE_PER_NIGHT = 9000;

    private BookingRules() {
    }

    public static int pricePence(final int nights) {
        if (nights < 1) {
            throw new IllegalArgumentException("nights must be at least 1");
        }
        return nights * PENCE_PER_NIGHT;
    }
}
