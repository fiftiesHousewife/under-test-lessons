package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;

/**
 * A lesson as one self-contained HTML slide deck: the intro slides, one slide per scene of the example (its narration
 * as the text), and the closing slides. Arrow keys move between slides; N shows the speaker notes; printing gives one
 * slide per page. Styled by the shared slide stylesheet, the deck stylesheet, then any user stylesheet.
 */
@Slf4j
final class SlideDeck {

    private static final Pattern SCENE = Pattern.compile("^#\\s*-+\\s*Scene\\s+\\d+:\\s*(.*?)\\s*-+\\s*$");
    private static final Pattern TITLE = Pattern.compile("^#\\s+(.+)$");

    private SlideDeck() {
    }

    /** The lesson named, or every lesson with a slides file when the name is "all". */
    static List<Media> lessons(final Path root, final String lesson) {
        if (!"all".equals(lesson)) {
            return List.of(new Media(root, lesson));
        }
        try (Stream<Path> folders = Files.list(root.resolve("lessons"))) {
            return folders.map(folder -> new Media(root, String.valueOf(folder.getFileName())))
                    .filter(media -> Files.isRegularFile(media.file(".slides.md"))).sorted(
                            java.util.Comparator.comparing(Media::safeLesson)).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Writes {@code <id>.deck.html} next to the lesson's video script, and the PDF too if asked. */
    static Path write(final Media media, final boolean pdf) {
        final Path deck = media.file(".deck.html");
        try {
            Files.writeString(deck, html(media));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (pdf) {
            Browser.printToPdf(deck, media.file(".deck.pdf"));
        }
        log.info("{}: slide deck written", media.safeLesson());
        return deck;
    }

    @SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION",
            justification = "Stylesheets, pictures and the controls are this repo's own files; slide text is escaped")
    static String html(final Media media) {
        final Slides slides = media.slides();
        final Path pictures = media.sharedSlides().resolve("pictures");
        final List<Path> sheets = new ArrayList<>(SlideDrawing.stylesheets(media));
        sheets.add(1, media.sharedSlides().resolve("deck.css"));
        final StringBuilder html = new StringBuilder("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>")
                .append(SlideHtml.escape(title(media))).append("</title><style>\n")
                .append(SlideHtml.styles(sheets)).append("</style></head><body class=\"deck\">")
                .append(SlideHtml.definitions(pictures));
        slides.intro().forEach(slide -> html.append(section(slide, pictures, "")));
        scenes(media).forEach(slide -> html.append(section(slide, pictures,
                "<p class=\"scene-text\">" + SlideHtml.escape(slide.narration()) + "</p>")));
        slides.closing().forEach(slide -> html.append(section(slide, pictures, "")));
        return html.append("<script>\n").append(SlideHtml.read(media.sharedSlides().resolve("deck.js")))
                .append("</script></body></html>\n").toString();
    }

    private static String section(final Slide slide, final Path pictures, final String paragraph) {
        return "<section class=\"deck-slide\">" + SlideHtml.body(slide, pictures, paragraph)
                + "<aside class=\"notes\">" + SlideHtml.escape(Pauses.withoutMarkers(slide.narration()))
                + "</aside></section>\n";
    }

    /** One slide per scene of the video script, titled by the scene, with its narration as the text. */
    static List<Slide> scenes(final Media media) {
        final Path tape = media.file(".tape");
        final Path narration = media.file(".narration.md");
        if (!Files.isRegularFile(tape) || !Files.isRegularFile(narration)) {
            return List.of();
        }
        final List<String> titles = matches(tape, SCENE);
        final List<String> paragraphs = Narration.paragraphs(narration);
        final List<Slide> scenes = new ArrayList<>();
        for (int i = 0; i < Math.min(titles.size(), paragraphs.size()); i++) {
            final String title = titles.get(i);
            scenes.add(new Slide(new Slide.Heading(Character.toUpperCase(title.charAt(0)) + title.substring(1),
                    "terminal"), List.of(), Pauses.withoutMarkers(paragraphs.get(i))));
        }
        return scenes;
    }

    /** The lesson's name, from the first heading of its README. */
    static String title(final Media media) {
        final Path readme = media.file(".tape").resolveSibling("../README.md").normalize();
        final List<String> headings = Files.isRegularFile(readme) ? matches(readme, TITLE) : List.of();
        return headings.isEmpty() ? media.safeLesson() : headings.getFirst();
    }

    private static List<String> matches(final Path file, final Pattern pattern) {
        try {
            final List<String> found = new ArrayList<>();
            for (final String line : Files.readAllLines(file)) {
                final Matcher match = pattern.matcher(line);
                if (match.matches()) {
                    found.add(match.group(1));
                }
            }
            return found;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
