package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A lesson's slides, from {@code <id>.slides.md}: intro slides play before the terminal recording, closing slides
 * after it. Each slide is a heading {@code ## intro: Title} or {@code ## closing: Title}, then {@code - } lines for
 * the points on screen (a point that is a URL is drawn as a link, and never spoken), then a {@code Say:} line
 * (continued on indented lines) for the narration.
 */
public record Slides(List<Slide> intro, List<Slide> closing) {

    /** Keeps its own copies of the lists. */
    public Slides {
        intro = List.copyOf(intro);
        closing = List.copyOf(closing);
    }

    /** The slides in a file, or none if there is no file. */
    public static Slides read(final Path file) {
        if (!Files.isRegularFile(file)) {
            return new Slides(List.of(), List.of());
        }
        final List<Slide> intro = new ArrayList<>();
        final List<Slide> closing = new ArrayList<>();
        Builder current = null;
        for (final String line : lines(file)) {
            if (line.startsWith("## ")) {
                add(current, intro, closing);
                current = Builder.from(line.substring(3).strip());
            } else if (current != null) {
                current.take(line);
            }
        }
        add(current, intro, closing);
        return new Slides(intro, closing);
    }

    private static void add(final Builder slide, final List<Slide> intro, final List<Slide> closing) {
        if (slide != null) {
            (slide.closing ? closing : intro).add(slide.build());
        }
    }

    /** Collects one slide's lines while reading. */
    private static final class Builder {
        private final boolean closing;
        private final String title;
        private final List<String> points = new ArrayList<>();
        private final StringBuilder narration = new StringBuilder();
        private boolean saying;

        private Builder(final boolean closing, final String title) {
            this.closing = closing;
            this.title = title;
        }

        static Builder from(final String heading) {
            final int colon = heading.indexOf(':');
            final String kind = colon < 0 ? "" : heading.substring(0, colon).strip();
            if (!"intro".equals(kind) && !"closing".equals(kind)) {
                throw new IllegalArgumentException("slide heading must start with intro: or closing: - " + heading);
            }
            return new Builder("closing".equals(kind), heading.substring(colon + 1).strip());
        }

        void take(final String line) {
            if (line.startsWith("- ")) {
                points.add(line.substring(2).strip());
                saying = false;
            } else if (line.startsWith("Say:")) {
                narration.append(line.substring(4).strip());
                saying = true;
            } else if (saying && line.startsWith(" ") && !line.isBlank()) {
                narration.append(' ').append(line.strip());
            }
        }

        Slide build() {
            if (narration.isEmpty()) {
                throw new IllegalArgumentException("slide \"" + title + "\" has no Say: line");
            }
            return new Slide(title, points, narration.toString());
        }
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
