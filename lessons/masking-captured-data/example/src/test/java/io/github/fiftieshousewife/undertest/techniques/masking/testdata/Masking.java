package io.github.fiftieshousewife.undertest.techniques.masking.testdata;

import io.github.fiftieshousewife.undertest.techniques.masking.Capture;
import io.github.fiftieshousewife.undertest.techniques.masking.CapturedFile;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * How the tests' data is made from a day of real data: the personal data replaced before the capture is kept with the
 * tests. This is part of the tests, not the code under test. A value nothing needs becomes the fixed text
 * {@code REMOVED}; an empty value stays empty.
 */
public interface Masking {

    String REMOVED = "REMOVED";
    UnaryOperator<String> REMOVE = value -> value.isEmpty() ? value : REMOVED;

    /** The day's data with its personal data replaced. */
    Capture mask(Capture capture);

    /** A copy of the file with each field that has a rule replaced by the rule's result; other fields kept. */
    static CapturedFile masked(final CapturedFile file, final Map<String, UnaryOperator<String>> rules) {
        final List<Map<String, String>> rows = file.rows().stream().map(row -> {
            final Map<String, String> masked = new LinkedHashMap<>(row);
            masked.replaceAll((field, value) -> rules.getOrDefault(field, UnaryOperator.identity()).apply(value));
            return masked;
        }).toList();
        return new CapturedFile(file.name(), file.header(), rows);
    }
}
