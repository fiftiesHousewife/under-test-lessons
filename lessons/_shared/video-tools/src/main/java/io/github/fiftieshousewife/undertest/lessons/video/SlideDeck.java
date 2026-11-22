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
        final List<Path> pictures = media.pictureFolders();
        final List<Path> sheets = new ArrayList<>(SlideDrawing.stylesheets(media));
        sheets.add(1, media.sharedSlides().resolve("deck.css"));
        final StringBuilder html = new StringBuilder("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>")
                .append(SlideHtml.escape(title(media))).append("</title><style>\n")
                .append(SlideHtml.styles(sheets)).append("</style></head><body class=\"deck\">")
                .append(SlideHtml.definitions(pictures));
        slides.intro().forEach(slide -> html.append(section(slide, pictures, "")));
        final List<Slide> scenes = scenes(media);
        final List<SceneContent> contents = Files.isRegularFile(media.file(".tape"))
                ? SceneContent.read(media.root(), media.file(".tape")) : List.of();
        for (int i = 0; i < scenes.size(); i++) {
            html.append(sceneSection(scenes.get(i), i < contents.size() ? contents.get(i) : new SceneContent()));
        }
        slides.closing().forEach(slide -> html.append(section(slide, pictures, "")));
        return html.append("<script>\n").append(SlideHtml.read(media.sharedSlides().resolve("deck.js")))
                .append("</script></body></html>\n").toString();
    }

    /** A scene of the example: its narration and callouts beside the code panels and commands it shows. */
    @SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION", justification = "Every piece of text is escaped")
    static String sceneSection(final Slide scene, final SceneContent content) {
        final StringBuilder html = new StringBuilder("<section class=\"deck-slide\"><main class=\"slide scene\">"
                + "<section class=\"text\"><h1>").append(SlideHtml.escape(scene.title()))
                .append("</h1><div class=\"rule\"></div><p class=\"scene-text\">")
                .append(SlideHtml.escape(scene.narration())).append("</p>");
        content.callouts().forEach(callout -> html.append("<p class=\"callout\">").append(SlideHtml.escape(callout))
                .append("</p>"));
        html.append("</section><figure class=\"code\" style=\"--code-size: ").append(codeSize(content))
                .append("px\">");
        for (final SceneContent.Panel panel : content.panels()) {
            html.append("<figcaption>").append(SlideHtml.escape(panel.label())).append("</figcaption><pre>")
                    .append(SlideHtml.escape(String.join("\n", panel.lines()))).append("</pre>");
        }
        content.commands().forEach(command -> html.append("<pre class=\"command\">$ ")
                .append(SlideHtml.escape(command)).append("</pre>"));
        return html.append("</figure></main><aside class=\"notes\">").append(SlideHtml.escape(scene.narration()))
                .append("</aside></section>\n").toString();
    }

    /** A font size, in pixels, that fits every panel's lines on the slide: at most 22, at least 12. */
    static int codeSize(final SceneContent content) {
        final List<String> lines = new ArrayList<>();
        content.panels().forEach(panel -> lines.addAll(panel.lines()));
        content.commands().forEach(command -> lines.add("$ " + command));
        final int rows = lines.size() + 2 * content.panels().size();
        final int widest = lines.stream().mapToInt(String::length).max().orElse(1);
        final double byHeight = 860.0 / Math.max(1, rows) / 1.35;
        final double byWidth = 1080.0 / Math.max(1, widest) / 0.6;
        return (int) Math.max(12, Math.min(22, Math.min(byHeight, byWidth)));
    }

    private static String section(final Slide slide, final List<Path> pictures, final String paragraph) {
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
                    ""), List.of(), Pauses.withoutMarkers(paragraphs.get(i))));
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
