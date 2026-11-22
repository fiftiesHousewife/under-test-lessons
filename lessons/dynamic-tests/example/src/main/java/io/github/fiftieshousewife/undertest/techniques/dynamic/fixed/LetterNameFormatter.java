package io.github.fiftieshousewife.undertest.techniques.dynamic.fixed;

import io.github.fiftieshousewife.undertest.techniques.dynamic.NameFormatter;
import java.util.Locale;

/** FIXED: a capital after every space, hyphen or apostrophe, so O'BRIEN becomes O'Brien. */
public final class LetterNameFormatter implements NameFormatter {

    @Override
    public String forLetter(final String record) {
        return NameFormatter.assemble(record, LetterNameFormatter::capitalise);
    }

    private static String capitalise(final String part) {
        final StringBuilder name = new StringBuilder(part.length());
        boolean startOfWord = true;
        for (final char c : part.toLowerCase(Locale.ROOT).toCharArray()) {
            name.append(startOfWord ? Character.toUpperCase(c) : c);
            startOfWord = c == ' ' || c == '-' || c == '\'';
        }
        return name.toString();
    }
}
