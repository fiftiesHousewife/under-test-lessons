package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * A printable script for a person to voice a lesson's deck, where no speech engine runs: one entry per clip, with the
 * file to save it as, the slide's title, the words with their pauses as directions, and hints from the pronunciation
 * list. Entry point: {@code RecordingScript <root> <lesson> [missing]}, which writes
 * {@code build/recording-scripts/<lesson>.html}; with {@code missing}, only the slides with no clip yet.
 */
@Slf4j
public final class RecordingScript {

    private RecordingScript() {
    }

    public static void main(final String... args) {
        if (args.length < 2 || args[1].isBlank()) {
            throw new IllegalArgumentException("usage: RecordingScript <root> <lesson> [missing]; name the lesson"
                    + " with -Plesson=<id>");
        }
        final Path root = Path.of(args[0]);
        final Media media = new Media(root, args[1]);
        final boolean missingOnly = args.length > 2 && "missing".equals(args[2]);
        final Path page = root.resolve("build/recording-scripts").resolve(media.safeLesson() + ".html");
        final int entries = write(media, missingOnly, page);
        log.info("{}: recording script with {} clip(s) written to build/recording-scripts/{}.html", media.safeLesson(),
                entries, media.safeLesson());
    }

    /** Writes the script to {@code page}; returns how many clips it lists. */
    static int write(final Media media, final boolean missingOnly, final Path page) {
        final List<Spoken> slides = DeckVoice.spoken(media);
        final List<String> titles = DeckVoice.titles(media);
        final Pronunciation pronunciation = DeckVoice.pronunciation(media);
        final Path clips = media.file(DeckVoice.FOLDER);
        final StringBuilder html = new StringBuilder(heading(media));
        int entries = 0;
        for (int i = 0; i < slides.size(); i++) {
            if (!missingOnly || DeckVoice.clips(clips, slides.get(i)).isEmpty()) {
                final String label = (i + 1) + ". " + (i < titles.size() ? titles.get(i) : "");
                html.append(entry(label, slides.get(i), hints(pronunciation.used(slides.get(i).written()))));
                entries++;
            }
        }
        html.append(entries == 0 ? "<p>Every slide has its clip: there is nothing to record.</p>" : "")
                .append("</body></html>\n");
        try {
            final Path pages = page.toAbsolutePath().getParent();
            if (pages != null) {
                Files.createDirectories(pages);
            }
            Files.writeString(page, html);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return entries;
    }

    /** The page's head, title and how to record. */
    private static String heading(final Media media) {
        final String title = SlideHtml.escape(SlideDeck.title(media));
        return "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Recording script: " + title
                + "</title><style>body{font:16px/1.5 sans-serif;max-width:46em;margin:2em auto;padding:0 1em}"
                + ".clip{border-top:1px solid #999;padding:.6em 0;break-inside:avoid}"
                + ".file{font-family:monospace}.words{font-size:1.2em}.pause{color:#666;font-style:italic}"
                + ".hints{color:#444}</style></head><body><h1>Recording script: " + title + "</h1><p>Record each clip"
                + " in one take, the same person, microphone and room throughout; trim the silence from both ends, and"
                + " level the loudness to about −16 LUFS. Save it in <span class=\"file\">"
                + SlideHtml.escape(media.name(DeckVoice.FOLDER)) + "/</span> under the name shown, as .m4a, .wav or"
                + " .mp3.</p>";
    }

    /** One clip to record: its number and title, its file name, its words, and its reading hints (already HTML). */
    private static String entry(final String label, final Spoken slide, final List<String> hints) {
        return "<div class=\"clip\"><p><b>" + SlideHtml.escape(label) + "</b> · save as <span"
                + " class=\"file\">" + slide.clipStem() + ".m4a</span></p><p class=\"words\">" + words(slide.written())
                + "</p>" + (hints.isEmpty() ? "" : "<p class=\"hints\">" + String.join(" · ", hints) + "</p>")
                + "</div>";
    }

    /** The words, with each pause marker written as a direction: "(pause 1.6 s)". */
    static String words(final String written) {
        final StringBuilder out = new StringBuilder();
        for (final Pauses.Part part : Pauses.parts(written)) {
            out.append(SlideHtml.escape(part.text()));
            if (part.pauseAfter() > 0) {
                out.append(" <span class=\"pause\">(pause ")
                        .append(String.format(Locale.ROOT, "%.1f", part.pauseAfter()).replace(".0", ""))
                        .append(" s)</span> ");
            }
        }
        return out.toString().strip();
    }

    /**
     * A reading hint for each listed word the slide uses: "PIT: say <i>pit</i>"; for a phrase given as sounds, the word
     * to stress ("version catalogue: stress <i>version</i>"); for one word given as sounds, the sounds themselves.
     */
    static List<String> hints(final Map<String, String> used) {
        final List<String> hints = new ArrayList<>();
        used.forEach((written, said) -> hints.add(SlideHtml.escape(written) + ": " + hint(written, said)));
        return hints;
    }

    private static String hint(final String written, final String said) {
        if (!said.contains("/")) {
            return "say <i>" + SlideHtml.escape(said) + "</i>";
        }
        final String[] words = written.split("\\s+");
        final String[] sounds = said.replace("/", "").strip().split("\\s+");
        final boolean oneRunOfSounds = said.chars().filter(c -> c == '/').count() == 2
                && said.startsWith("/") && said.endsWith("/");
        if (oneRunOfSounds && sounds.length > 1 && words.length == sounds.length) {
            for (int i = 0; i < sounds.length; i++) {
                if (sounds[i].contains("ˈ")) {
                    return "stress <i>" + SlideHtml.escape(words[i]) + "</i>";
                }
            }
        }
        return "say it as <i>" + SlideHtml.escape(said) + "</i>";
    }
}
