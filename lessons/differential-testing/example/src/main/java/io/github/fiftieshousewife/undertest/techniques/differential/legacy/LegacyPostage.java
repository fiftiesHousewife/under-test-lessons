package io.github.fiftieshousewife.undertest.techniques.differential.legacy;

import io.github.fiftieshousewife.undertest.techniques.differential.Postage;

/** The old calculator, in use for years. Nobody wants to change it, but its answers are the ones customers know. */
public final class LegacyPostage implements Postage {

    @Override
    public int pence(final int grams) {
        if (grams <= 0 || grams > MAX_GRAMS) {
            throw new IllegalArgumentException("a parcel weighs from 1 g to 5 kg");
        }
        if (grams <= 100) {
            return 85;
        } else if (grams <= 250) {
            return 155;
        } else if (grams <= 500) {
            return 195;
        } else if (grams <= 1000) {
            return 315;
        }
        return 315 + (grams - 1000 + 499) / 500 * 120;
    }
}
