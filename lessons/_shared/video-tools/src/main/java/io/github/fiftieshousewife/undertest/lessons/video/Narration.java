package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reads a lesson's narration file: a numbered list with one item per scene. Markdown emphasis and code marks are
 * removed, so the text can be shown as captions or read aloud.
 */
public final class Narration {

    private static final Pattern ITEM = Pattern.compile("^\\d+\\.\\s+(.*)$");

    private Narration() {
    }

    /** One plain-text paragraph per scene, in order. */
    public static List<String> paragraphs(final Path file) {
        return rawParagraphs(file).stream().map(Narration::plain).toList();
    }

    /* One paragraph per scene, exactly as written (Markdown marks kept). */
    static List<String> rawParagraphs(final Path file) {
        final List<StringBuilder> items = new ArrayList<>();
        for (final String line : readLines(file)) {
            final var item = ITEM.matcher(line);
            if (item.matches()) {
                items.add(new StringBuilder(item.group(1).strip()));
            } else if (!items.isEmpty() && line.startsWith(" ") && !line.isBlank()) {
                items.getLast().append(' ').append(line.strip());
            }
        }
        return items.stream().map(StringBuilder::toString).toList();
    }

    static String plain(final String markdown) {
        return markdown.replace("**", "").replace("`", "").replaceAll("\\s+", " ").strip();
    }

    private static List<String> readLines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
