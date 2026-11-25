package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Checks a deck's voice clips against its slides: a slide with no clip, a clip no slide uses, and a clip much longer or
 * shorter than its words take to say, as when it was read from other words or cut short. Unused clips are removed only
 * when asked, since each may be someone's recording. A clip shorter than {@code TOO_SHORT}, or longer than
 * {@code TOO_LONG}, times what its words take at the voice's speed is reported.
 */
final class DeckClips {

    static final double TOO_SHORT = 0.5;
    static final double TOO_LONG = 1.6;

    /* What is wrong (missing clips, where the voice can be run) and what is only worth a look. */
    record Findings(List<Problem> problems, List<Problem> warnings) {
    }

    private DeckClips() {
    }

    /*
     * The lesson's clips checked against its slides. A missing clip is a problem where {@code missingFails}, else a
     * warning, as in a company's copy, where a person records it. Unused clips are removed if {@code removeUnused}.
     */
    static Findings check(final Media media, final boolean missingFails, final boolean removeUnused) {
        final Path folder = media.file(DeckVoice.FOLDER);
        final List<Spoken> slides = DeckVoice.spoken(media);
        final List<Problem> problems = new ArrayList<>();
        final List<Problem> warnings = new ArrayList<>();
        final List<Problem> missing = missingFails ? problems : warnings;
        for (int i = 0; i < slides.size(); i++) {
            final List<Path> found = DeckVoice.clips(folder, slides.get(i));
            if (found.isEmpty()) {
                missing.add(missing(folder, i + 1, slides.get(i)));
            } else {
                warnings.addAll(clipWarnings(i + 1, slides.get(i), found));
            }
        }
        warnings.addAll(unused(folder, slides, removeUnused));
        return new Findings(problems, warnings);
    }

    /* Clips no slide uses: removed if {@code removeUnused}, else each one a warning. */
    private static List<Problem> unused(final Path folder, final List<Spoken> slides, final boolean removeUnused) {
        final Set<String> used = slides.stream().map(Spoken::clipStem).collect(Collectors.toSet());
        final List<String> unused = clips(folder).stream()
                .filter(name -> !used.contains(name.substring(0, name.lastIndexOf('.')))).toList();
        if (removeUnused && !unused.isEmpty()) {
            remove(folder, unused);
            return List.of();
        }
        return unused.stream().map(name -> new Problem(folder.resolve(name), "no slide uses this clip; remove"
                + " it with -PremoveUnused=true")).toList();
    }

    private static Problem missing(final Path folder, final int number, final Spoken slide) {
        return new Problem(folder, "slide " + number + " (\"" + opening(slide.written()) + "\") has no clip "
                + slide.clipName() + "; voice the deck, or record it");
    }

    /* A slide with clips in two formats, and a clip far longer or shorter than its words. */
    private static List<Problem> clipWarnings(final int number, final Spoken slide, final List<Path> found) {
        final List<Problem> warnings = new ArrayList<>();
        final Path clip = found.getFirst();
        if (found.size() > 1) {
            warnings.add(new Problem(clip, "slide " + number + " has " + found.size() + " clips, "
                    + found.stream().map(file -> String.valueOf(file.getFileName())).collect(Collectors.joining(
                            " and ")) + "; the deck plays " + clip.getFileName() + ", so remove the other"));
        }
        final double expected = VideoLength.spoken(slide.written());
        final double seconds = ClipLength.seconds(clip);
        if (seconds >= 0 && (seconds < expected * TOO_SHORT || seconds > expected * TOO_LONG)) {
            warnings.add(new Problem(clip, String.format(Locale.ROOT, "slide %d (\"%s\") takes %.1f s, but its"
                    + " words take about %.1f s to say; check it says the slide's words, whole", number,
                    opening(slide.written()), seconds, expected)));
        }
        return warnings;
    }

    /*
     * Who made the clips the deck plays, as one line: "15 by the voice kokoro-onnx-…", "2 by Jane Smith", "1 by a
     * person". Empty where the deck has no clips.
     */
    static String voices(final Media media) {
        final Path folder = media.file(DeckVoice.FOLDER);
        final List<String> playing = DeckVoice.spoken(media).stream()
                .flatMap(slide -> DeckVoice.clips(folder, slide).stream().limit(1))
                .map(clip -> String.valueOf(clip.getFileName())).toList();
        return ClipLedger.read(folder).mix(playing).entrySet().stream()
                .map(by -> by.getValue() + " by " + by.getKey()).collect(Collectors.joining(", "));
    }

    /* The first few words, to name a slide by. */
    private static String opening(final String written) {
        final String[] words = Pauses.withoutMarkers(written).strip().split("\\s+");
        final String start = String.join(" ", List.of(words).subList(0, Math.min(6, words.length)));
        return words.length > 6 ? start + "…" : start;
    }

    private static List<String> clips(final Path folder) {
        if (!Files.isDirectory(folder)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(folder)) {
            return files.map(file -> String.valueOf(file.getFileName()))
                    .filter(name -> DeckVoice.FORMATS.stream().anyMatch(name::endsWith)).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void remove(final Path folder, final List<String> unused) {
        final ClipLedger ledger = ClipLedger.read(folder);
        try {
            for (final String name : unused) {
                Files.delete(folder.resolve(name));
                ledger.forget(name);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        ledger.write();
    }
}
