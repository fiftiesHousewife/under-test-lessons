package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        final List<String> sections = sections(media);
        final List<String> voice = DeckVoice.sources(media);
        final StringBuilder html = head(media);
        for (int i = 0; i < sections.size(); i++) {
            html.append(DeckVoice.withVoice(sections.get(i), i < voice.size() ? voice.get(i) : ""));
        }
        if (sections.stream().anyMatch(section -> section.contains("live-run cast"))) {
            html.append(Casts.PLAYER);
        }
        return html.append("<script>\n").append(SlideHtml.read(media.sharedSlides().resolve("deck.js")))
                .append("</script></body></html>\n").toString();
    }

    @SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION",
            justification = "Stylesheets, pictures and the controls are this repo's own files; slide text is escaped")
    private static StringBuilder head(final Media media) {
        final List<Path> sheets = new ArrayList<>(SlideDrawing.stylesheets(media));
        sheets.add(1, media.sharedSlides().resolve("deck.css"));
        return new StringBuilder("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>")
                .append(SlideHtml.escape(title(media))).append("</title><style>\n")
                .append(SlideHtml.styles(sheets)).append("</style></head><body class=\"deck ")
                .append(SlideHtml.frameClass()).append("\">")
                .append(SlideHtml.definitions(media.pictureFolders()));
    }

    /* Every slide's section, in order: the intro slides, one per scene, and the closing slides. */
    private static List<String> sections(final Media media) {
        final Slides slides = media.slides();
        final List<Path> pictures = media.pictureFolders();
        final List<String> sections = new ArrayList<>();
        slides.intro().forEach(slide -> sections.add(section(slide, pictures, "")));
        final List<Slide> scenes = scenes(media);
        final List<SceneContent> contents = Files.isRegularFile(media.file(".tape"))
                ? SceneContent.read(media.root(), media.file(".tape")) : List.of();
        int runs = 0;
        for (int i = 0; i < scenes.size(); i++) {
            final SceneContent content = i < contents.size() ? contents.get(i) : new SceneContent();
            final Optional<String> run = content.commands().isEmpty() ? Optional.empty() : Casts.of(media, runs + 1);
            runs += content.commands().size();
            sections.add(SceneSlide.section(scenes.get(i), content, run));
        }
        slides.closing().forEach(slide -> sections.add(section(slide, pictures, "")));
        return sections;
    }

    private static String section(final Slide slide, final List<Path> pictures, final String paragraph) {
        return "<section class=\"deck-slide\">" + SlideHtml.body(slide, pictures, paragraph)
                + "<aside class=\"notes\">" + SlideHtml.escape(Pauses.withoutMarkers(slide.narration()))
                + "</aside></section>\n";
    }

    /* One slide per scene of the video script, titled by the scene, with its narration as the text. */
    static List<Slide> scenes(final Media media) {
        final List<String> paragraphs = sceneNarrations(media);
        if (paragraphs.isEmpty()) {
            return List.of();
        }
        final List<String> titles = matches(media.file(".tape"), SCENE);
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
