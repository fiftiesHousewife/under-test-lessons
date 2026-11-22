package io.github.fiftieshousewife.undertest.techniques.dynamic.today;

import io.github.fiftieshousewife.undertest.techniques.dynamic.NameFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/** TODAY: capitalises the first letter of each word, so O'BRIEN becomes O'brien. */
public final class TodaysNameFormatter implements NameFormatter {

    @Override
    public String forLetter(final String record) {
        return NameFormatter.assemble(record, part -> Arrays.stream(part.split(" "))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" ")));
    }
}
