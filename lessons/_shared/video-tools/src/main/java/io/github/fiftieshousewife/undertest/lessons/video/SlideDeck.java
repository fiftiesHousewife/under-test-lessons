package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;

/*
 * A lesson as one self-contained HTML slide deck: the intro slides, one slide per scene of the example (its narration
 * as the text), and the closing slides. Arrow keys move between slides; N shows the speaker notes; printing gives one
 * slide per page. Styled by the shared slide stylesheet, the deck stylesheet, then any user stylesheet.
 */
@Slf4j
final class SlideDeck {

    private static final Pattern SCENE = Pattern.compile("^#\\s*-+\\s*Scene\\s+\\d+:\\s*(.*?)\\s*-+\\s*$");
    private static final Pattern HEADING = Pattern.compile("^#\\s+(.+)$");

    private SlideDeck() {
    }

    /* The lesson named, or every lesson with a slides file when the name is "all". */
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

    /* Writes {@code <id>.deck.html} next to the lesson's video script, and the PDF too if asked. */
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
                .append(SlideHtml.styles(sheets)).append("</style></head><body class=\"deck ")
                .append(SlideHtml.frameClass()).append("\">")
                .append(SlideHtml.definitions(pictures));
        final List<String> sections = new ArrayList<>();
        slides.intro().forEach(slide -> sections.add(section(slide, pictures, "")));
        final List<Slide> scenes = scenes(media);
        final List<SceneContent> contents = Files.isRegularFile(media.file(".tape"))
                ? SceneContent.read(media.root(), media.file(".tape")) : List.of();
        for (int i = 0; i < scenes.size(); i++) {
            sections.add(sceneSection(scenes.get(i), i < contents.size() ? contents.get(i) : new SceneContent()));
        }
        slides.closing().forEach(slide -> sections.add(section(slide, pictures, "")));
        final List<String> voice = DeckVoice.sources(media);
        for (int i = 0; i < sections.size(); i++) {
            html.append(withVoice(sections.get(i), i < voice.size() ? voice.get(i) : ""));
        }
        return html.append("<script>\n").append(SlideHtml.read(media.sharedSlides().resolve("deck.js")))
                .append("</script></body></html>\n").toString();
    }

    /* The slide's section with its spoken clip, if it has one, played by the deck's Listen control. */
    static String withVoice(final String section, final String source) {
        if (source.isEmpty()) {
            return section;
        }
        final int end = section.lastIndexOf("</section>");
        return section.substring(0, end) + "<audio class=\"voice\" preload=\"none\" src=\""
                + SlideHtml.escape(source) + "\"></audio>" + section.substring(end);
    }

    /*
     * A scene of the example: its caption as points, and its callouts, beside the code panels and commands it shows;
     * the narration, which the voice reads, is in the speaker notes. A scene with no caption shows its narration.
     */
    @SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION", justification = "Every piece of text is escaped")
    static String sceneSection(final Slide scene, final SceneContent content) {
        final StringBuilder html = new StringBuilder("<section class=\"deck-slide\"><main class=\"slide scene\">"
                + "<section class=\"text\"><h1>").append(SlideHtml.escape(scene.title()))
                .append("</h1><div class=\"rule\"></div>");
        final List<String> points = points(scene.title(), content.notes());
        if (points.isEmpty()) {
            html.append("<p class=\"scene-text\">").append(SlideHtml.escape(scene.narration())).append("</p>");
        } else {
            html.append("<ul class=\"scene-points\">");
            points.forEach(point -> html.append("<li class=\"point\">").append(SlideHtml.escape(point))
                    .append("</li>"));
            html.append("</ul>");
        }
        content.callouts().forEach(callout -> html.append("<p class=\"callout\">").append(SlideHtml.escape(callout))
                .append("</p>"));
        html.append("</section><figure class=\"code\" style=\"--code-size: ").append(codeSize(content))
                .append("px\">");
        for (final SceneContent.Panel panel : content.panels()) {
            html.append("<figcaption>").append(SlideHtml.escape(panel.label())).append("</figcaption><pre>")
                    .append(String.join("\n", panel.lines().stream()
                            .map(line -> marked(line, panel.marked().contains(LiveOutputs.lineNumber(line))))
                            .toList()))
                    .append("</pre>");
        }
        final List<List<String>> outputs = content.outputs();
        for (int i = 0; i < content.commands().size(); i++) {
            html.append("<pre class=\"command\">$ ").append(SlideHtml.escape(content.commands().get(i)))
                    .append("</pre>");
            if (i < outputs.size() && !outputs.get(i).isEmpty()) {
                final Set<Integer> keyLines = LiveOutputs.marked(content);
                html.append("<pre class=\"output\">").append(String.join("\n", outputs.get(i).stream()
                        .map(line -> marked(line, LiveOutputs.namesAny(line, keyLines))).toList())).append("</pre>");
            }
        }
        return html.append("</figure></main><aside class=\"notes\">").append(SlideHtml.escape(scene.narration()))
                .append("</aside></section>\n").toString();
    }

    /*
     * A scene's captions as points: each sentence or clause after a colon its own point, without the emphasis marks,
     * and without a first clause that only repeats the slide's title ("The code under test: ...").
     */
    static List<String> points(final String title, final List<String> notes) {
        final String heading = capitalised(title.strip());
        final List<String> points = new ArrayList<>();
        for (final String note : notes) {
            for (final String part : note.replace("*", "").split("(?<=[.:])\\s+")) {
                final String point = part.strip().replaceAll("[.:]$", "").strip();
                if (!point.isEmpty() && !capitalised(point).equals(heading)) {
                    points.add(capitalised(point));
                }
            }
        }
        return points;
    }

    private static String capitalised(final String text) {
        return text.isEmpty() ? text : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /* One line of a panel or output, escaped, and highlighted when it is one the scene is about. */
    static String marked(final String line, final boolean key) {
        return key ? "<mark>" + SlideHtml.escape(line) + "</mark>" : SlideHtml.escape(line);
    }

    /* A font size, in pixels, that fits every panel's lines on the slide: at most 22, at least 12. */
    static int codeSize(final SceneContent content) {
        final List<String> lines = new ArrayList<>();
        content.panels().forEach(panel -> lines.addAll(panel.lines()));
        content.commands().forEach(command -> lines.add("$ " + command));
        content.outputs().forEach(lines::addAll);
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

    /* One slide per scene of the video script, titled by the scene, with its narration as the text. */
    static List<Slide> scenes(final Media media) {
        final Path tape = media.file(".tape");
        final Path narration = media.file(".narration.md");
        if (!Files.isRegularFile(tape) || !Files.isRegularFile(narration)) {
            return List.of();
        }
        final List<String> titles = matches(tape, SCENE);
        final List<String> paragraphs = sceneNarrations(media);
        final List<Slide> scenes = new ArrayList<>();
        for (int i = 0; i < paragraphs.size(); i++) {
            final String title = titles.get(i);
            scenes.add(new Slide(new Slide.Heading(Character.toUpperCase(title.charAt(0)) + title.substring(1),
                    ""), List.of(), Pauses.withoutMarkers(paragraphs.get(i))));
        }
        return scenes;
    }

    /* Each scene's narration as spoken, pause markers kept: one per scene that has both a title and narration. */
    static List<String> sceneNarrations(final Media media) {
        final Path tape = media.file(".tape");
        final Path narration = media.file(".narration.md");
        if (!Files.isRegularFile(tape) || !Files.isRegularFile(narration)) {
            return List.of();
        }
        final List<String> paragraphs = Narration.paragraphs(narration);
        return paragraphs.subList(0, Math.min(matches(tape, SCENE).size(), paragraphs.size()));
    }

    /* The lesson's name, from the first heading of its README. */
    static String title(final Media media) {
        final Path readme = media.file(".tape").resolveSibling("../README.md").normalize();
        final List<String> headings = Files.isRegularFile(readme) ? matches(readme, HEADING) : List.of();
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
