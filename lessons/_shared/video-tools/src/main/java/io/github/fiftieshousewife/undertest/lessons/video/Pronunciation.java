package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * How the voice should say words it would otherwise get wrong, such as "PIT" read as letters. Applied only to the
 * spoken narration; captions keep the written form. The file has one {@code word = how to say it} per line, and lines
 * starting with {@code #} are comments.
 */
final class Pronunciation {

    private final Map<String, String> spoken;

    private Pronunciation(final Map<String, String> spoken) {
        this.spoken = spoken;
    }

    /** Reads the pronunciation file, or returns one that changes nothing if there is no file. */
    static Pronunciation read(final Path file) {
        final Map<String, String> spoken = new LinkedHashMap<>();
        if (Files.isRegularFile(file)) {
            for (final String line : lines(file)) {
                final int equals = line.indexOf('=');
                if (!line.isBlank() && !line.strip().startsWith("#") && equals > 0) {
                    spoken.put(line.substring(0, equals).strip(), line.substring(equals + 1).strip());
                }
            }
        }
        return new Pronunciation(spoken);
    }

    /** The text with each listed word (whole words only, matching case) replaced by how to say it. */
    String forSpeech(final String text) {
        String result = text;
        for (final Map.Entry<String, String> word : spoken.entrySet()) {
            result = Pattern.compile("\\b" + Pattern.quote(word.getKey()) + "\\b").matcher(result)
                    .replaceAll(Matcher.quoteReplacement(word.getValue()));
        }
        return result;
    }

    private static java.util.List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
