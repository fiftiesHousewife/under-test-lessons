package io.github.fiftieshousewife.undertest.techniques.differential.rewrite;

import io.github.fiftieshousewife.undertest.techniques.differential.Postage;
import java.util.List;

/** The rewrite: the same prices, read from a price list instead of a chain of ifs. */
public final class RewrittenPostage implements Postage {

    private static final int EXTRA_PENCE_PER_HALF_KILO = 120;

    private final List<Band> bands;

    public RewrittenPostage(final List<Band> bands) {
        this.bands = List.copyOf(bands);
    }

    @Override
    public int pence(final int grams) {
        if (grams <= 0 || grams > MAX_GRAMS) {
            throw new IllegalArgumentException("a parcel weighs from 1 g to 5 kg");
        }
        for (final Band band : bands) {
            if (grams <= band.upToGrams()) {
                return band.pence();
            }
        }
        final Band last = bands.getLast();
        final int extraHalfKilos = (grams - last.upToGrams() + 499) / 500;
        return last.pence() + extraHalfKilos * EXTRA_PENCE_PER_HALF_KILO;
    }
}
