package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Reads a lesson's narration file: a numbered list with one item per scene. Markdown emphasis and code marks are
 * removed, so the text can be shown on a slide or read aloud.
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
            } else if (!items.isEmpty() && continuesItem(line)) {
                items.getLast().append(' ').append(line.strip());
            }
        }
        return items.stream().map(StringBuilder::toString).toList();
    }

    private static boolean continuesItem(final String line) {
        return line.startsWith(" ") && !line.isBlank();
    }

    static String plain(final String markdown) {
        return markdown.replace("**", "").replace("`", "").replaceAll("\\s+", " ").strip();
    }

    /* The deck and its voice pair each scene with one narration item, so the counts must match. */
    static Optional<Problem> checkSceneCount(final Path sceneFile, final Path narration) {
        final int scenes = SceneContent.titles(sceneFile).size();
        final int items = Narration.rawParagraphs(narration).size();
        return scenes == items ? Optional.empty() : Optional.of(new Problem(sceneFile, scenes + " scenes but " + items
                + " narration items; each scene needs exactly one item (look for a repeated scene heading)"));
    }

    /*
     * Narration is spoken, so it must never contain a command (say what happens instead), nor a semicolon, which the
     * voice reads as a run-on sentence (use a full stop).
     */
    static List<Problem> checkNarration(final Path narration) {
        final List<String> paragraphs = Narration.rawParagraphs(narration);
        final List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < paragraphs.size(); i++) {
            final String text = paragraphs.get(i);
            if (namesACommand(text)) {
                problems.add(new Problem(narration, "item " + (i + 1) + " contains a command, which the voice would"
                        + " read aloud; say what happens instead (for example, \"Now we run the strong tests\")"));
            }
            if (text.contains(";")) {
                problems.add(new Problem(narration, "item " + (i + 1) + " " + SlideFileChecks.SEMICOLON));
            }
        }
        return problems;
    }

    private static boolean namesACommand(final String text) {
        return text.contains("`") || text.contains("./") || text.contains("gradlew");
    }

    private static List<String> readLines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
