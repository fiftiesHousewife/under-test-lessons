package io.github.fiftieshousewife.undertest.lessons.checks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reading files, and the ids a pattern finds in a piece of text. */
final class Text {

    private Text() {
    }

    static String read(final Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Every first group the pattern finds in the text, sorted. */
    static Set<String> matches(final Pattern pattern, final String text) {
        final Set<String> found = new TreeSet<>();
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }
}
