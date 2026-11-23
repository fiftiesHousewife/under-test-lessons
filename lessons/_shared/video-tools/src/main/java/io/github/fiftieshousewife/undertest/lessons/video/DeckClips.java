package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Checks a deck's voice clips against its slides: a slide with no clip, a clip no slide uses, and a clip much longer
 * or shorter than its words take to say, as when it was read from other words or cut short. Unused clips are removed
 * only when asked, since each may be someone's recording.
 */
final class DeckClips {

    /** A clip this much shorter or longer than its words take at the voice's speed is reported. */
    static final double TOO_SHORT = 0.5;
    static final double TOO_LONG = 1.6;

    /** What is wrong (missing clips, where the voice can be run) and what is only worth a look. */
    record Findings(List<Problem> problems, List<Problem> warnings) {
    }

    private DeckClips() {
    }

    /**
     * The lesson's clips checked against its slides. A missing clip is a problem where {@code missingFails}, else a
     * warning, as in a company's copy, where a person records it. Unused clips are removed if {@code removeUnused}.
     */
    static Findings check(final Media media, final boolean missingFails, final boolean removeUnused) {
        final Path folder = media.file(DeckVoice.FOLDER);
        final List<Spoken> slides = DeckVoice.spoken(media);
        final List<Problem> problems = new ArrayList<>();
        final List<Problem> warnings = new ArrayList<>();
        for (int i = 0; i < slides.size(); i++) {
            final Spoken slide = slides.get(i);
            final Path clip = folder.resolve(slide.clipName());
            if (!Files.isRegularFile(clip)) {
                (missingFails ? problems : warnings).add(new Problem(folder, "slide " + (i + 1) + " (\""
                        + opening(slide.written()) + "\") has no clip " + slide.clipName()
                        + "; voice the deck, or record it"));
                continue;
            }
            final double expected = VideoLength.spoken(slide.written());
            final double seconds = Mp4Length.seconds(clip);
            if (seconds < expected * TOO_SHORT || seconds > expected * TOO_LONG) {
                warnings.add(new Problem(clip, String.format(Locale.ROOT, "slide %d (\"%s\") takes %.1f s, but its"
                        + " words take about %.1f s to say; check it says the slide's words, whole", i + 1,
                        opening(slide.written()), seconds, expected)));
            }
        }
        final Set<String> used = slides.stream().map(Spoken::clipName).collect(Collectors.toSet());
        final List<String> unused = clips(folder).stream().filter(name -> !used.contains(name)).toList();
        if (removeUnused && !unused.isEmpty()) {
            remove(folder, unused);
        } else {
            unused.forEach(name -> warnings.add(new Problem(folder.resolve(name), "no slide uses this clip; remove"
                    + " it with -PremoveUnused=true")));
        }
        return new Findings(problems, warnings);
    }

    /** The first few words, to name a slide by. */
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
            return files.map(file -> String.valueOf(file.getFileName())).filter(name -> name.endsWith(".m4a"))
                    .sorted().toList();
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

    /** A clip's length, read from its MP4 header ({@code moov/mvhd}), so no player or ffmpeg is needed. */
    static final class Mp4Length {

        private Mp4Length() {
        }

        static double seconds(final Path clip) {
            final ByteBuffer data;
            try {
                data = ByteBuffer.wrap(Files.readAllBytes(clip));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            final int moov = box(data, 0, data.limit(), "moov");
            final int mvhd = moov < 0 ? -1 : box(data, moov + 8, moov + (int) size(data, moov), "mvhd");
            if (mvhd < 0) {
                throw new IllegalArgumentException(clip + " is not an MP4 sound file: it has no moov/mvhd header");
            }
            final int at = mvhd + 8;
            final boolean wide = data.get(at) == 1;
            final long timescale = Integer.toUnsignedLong(data.getInt(at + (wide ? 20 : 12)));
            final long duration = wide ? data.getLong(at + 24) : Integer.toUnsignedLong(data.getInt(at + 16));
            return (double) duration / timescale;
        }

        /** Where the box of this type starts, among the boxes from {@code start} to {@code end}, or -1. */
        private static int box(final ByteBuffer data, final int start, final int end, final String type) {
            int at = start;
            while (at + 8 <= end) {
                final long size = size(data, at);
                if (type.equals(new String(new byte[] {data.get(at + 4), data.get(at + 5), data.get(at + 6),
                        data.get(at + 7)}, java.nio.charset.StandardCharsets.US_ASCII))) {
                    return at;
                }
                if (size < 8) {
                    return -1;
                }
                at += (int) size;
            }
            return -1;
        }

        private static long size(final ByteBuffer data, final int at) {
            final long size = Integer.toUnsignedLong(data.getInt(at));
            if (size == 1) {
                return data.getLong(at + 8);
            }
            return size == 0 ? data.limit() - at : size;
        }
    }
}
