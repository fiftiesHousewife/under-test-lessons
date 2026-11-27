package io.github.fiftieshousewife.undertest.techniques.fuzzing.fixed;

import io.github.fiftieshousewife.undertest.techniques.fuzzing.Fields;
import io.github.fiftieshousewife.undertest.techniques.fuzzing.Label;
import io.github.fiftieshousewife.undertest.techniques.fuzzing.LabelReader;
import java.util.List;

/**
 * FIXED: every way a line can be wrong is refused, with the reason, as the contract says: the number of fields, a
 * postcode with no space, a weight that isn't a whole number, and a weight the courier won't take.
 */
public final class FixedLabelReader implements LabelReader {

    private static final int LIGHTEST_GRAMS = 1;
    private static final int HEAVIEST_GRAMS = 30_000;

    @Override
    public Label read(final String line) {
        final List<String> fields = Fields.of(line);
        if (fields.size() != 3) {
            throw new IllegalArgumentException("a label has 3 fields, not " + fields.size());
        }
        final String postcode = fields.get(1);
        final int space = postcode.indexOf(' ');
        if (space <= 0) {
            throw new IllegalArgumentException("a postcode has a space between its halves: " + postcode);
        }
        return new Label(fields.get(0), postcode.substring(0, space), grams(fields.get(2)));
    }

    private static int grams(final String weight) {
        try {
            return takenByTheCourier(Integer.parseInt(weight));
        } catch (NumberFormatException notANumber) {
            throw new IllegalArgumentException("a weight is a whole number of grams: " + weight, notANumber);
        }
    }

    private static int takenByTheCourier(final int grams) {
        if (grams < LIGHTEST_GRAMS || grams > HEAVIEST_GRAMS) {
            throw new IllegalArgumentException("the courier takes 1 to 30,000 grams, not " + grams);
        }
        return grams;
    }
}
