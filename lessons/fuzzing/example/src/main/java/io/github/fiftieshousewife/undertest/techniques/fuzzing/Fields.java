package io.github.fiftieshousewife.undertest.techniques.fuzzing;

import java.util.ArrayList;
import java.util.List;

/** Splits a label line at its semicolons, as the old code always has: one character at a time. */
public final class Fields {

    private static final char SEPARATOR = ';';

    private Fields() {
    }

    /** The line's fields, each trimmed; an empty field stays as an empty string. */
    public static List<String> of(final String line) {
        final List<String> fields = new ArrayList<>();
        final StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            final char c = line.charAt(i);
            if (c == SEPARATOR) {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }
}
