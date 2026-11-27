package io.github.fiftieshousewife.undertest.techniques.fuzzing.today;

import io.github.fiftieshousewife.undertest.techniques.fuzzing.Fields;
import io.github.fiftieshousewife.undertest.techniques.fuzzing.Label;
import io.github.fiftieshousewife.undertest.techniques.fuzzing.LabelReader;
import java.util.List;

/**
 * TODAY: the reader as it has run for years. It checks the number of fields, and that the weight is a number. It
 * takes the postcode's area up to its space, and every postcode it has ever been tested with had one.
 */
public final class TodaysLabelReader implements LabelReader {

    @Override
    public Label read(final String line) {
        final List<String> fields = Fields.of(line);
        if (fields.size() != 3) {
            throw new IllegalArgumentException("a label has 3 fields, not " + fields.size());
        }
        final String postcode = fields.get(1);
        final String area = postcode.substring(0, postcode.indexOf(' '));
        return new Label(fields.get(0), area, Integer.parseInt(fields.get(2)));
    }
}
