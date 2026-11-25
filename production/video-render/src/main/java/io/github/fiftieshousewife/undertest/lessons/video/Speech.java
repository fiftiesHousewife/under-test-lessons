package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*
 * A piece of narration for the voice to read, as the parts between its pause markers: each part's text is written to a
 * file the voice reads, and once it has, the parts are joined with the pauses' silence into the clip. Many pieces can
 * be read in one go ({@link #readAll}), so a voice that reads a list loads its model once.
 */
final class Speech {

    /*
     * The longest gap the voice may leave between sentences, in seconds: it leaves up to a second, which drags. The
     * pauses the narration asks for ({@code [pause 1600ms]}) are kept whole. Part of every clip's key
     * ({@link Spoken#saidKey}), so changing it reads each clip again.
     */
    static final double LONGEST_GAP = 0.4;

    /* Set to true when the voice command also takes {@code --list <file>}, one text file and WAV a line. */
    static final String READS_LISTS = "video.voiceReadsLists";

    /*
     * The voice engine, as the build names it: its program, voice, speed, model and packages, in one word. A clip the
     * voice made with another engine is read again (see {@link ClipLedger}).
     */
    static final String ENGINE_PROPERTY = "video.voiceEngine";

    /* One part for the voice to read: the text file it reads, and the WAV it writes. */
    record Reading(Path text, Path wav) {
    }

    private final List<Pauses.Part> parts;
    private final List<Reading> readings;
    private final Path clip;

    private Speech(final List<Pauses.Part> parts, final List<Reading> readings, final Path clip) {
        this.parts = parts;
        this.readings = readings;
        this.clip = clip;
    }

    /* The voice engine this run uses, from {@link #ENGINE_PROPERTY}; "unnamed" where the build names none. */
    static String engine() {
        return System.getProperty(ENGINE_PROPERTY, "unnamed");
    }

    /* The narration's parts, their text written beside {@code clip}, ready for the voice to read. */
    static Speech of(final String narration, final Path clip) {
        final List<Pauses.Part> parts = Pauses.parts(narration);
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("a scene has no narration: " + clip);
        }
        final List<Reading> readings = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            final Path part = clip.resolveSibling(clip.getFileName() + "-" + (i + 1));
            final Reading reading = new Reading(part.resolveSibling(part.getFileName() + ".txt"),
                    part.resolveSibling(part.getFileName() + ".wav"));
            write(reading.text(), parts.get(i).text());
            readings.add(reading);
        }
        return new Speech(parts, readings, clip);
    }

    /*
     * Has the voice read every part of every piece: in one run if it reads lists, else one run a part. Returns each
     * piece's clip, its parts joined.
     */
    static List<WavFile> readAll(final List<String> command, final List<Speech> pieces) {
        final List<Reading> all = pieces.stream().flatMap(piece -> piece.readings.stream()).toList();
        if (all.isEmpty()) {
            return List.of();
        }
        if (Boolean.getBoolean(READS_LISTS)) {
            final Path list = all.getFirst().text().resolveSibling("voice-list.txt");
            final StringBuilder lines = new StringBuilder();
            all.forEach(reading -> lines.append(reading.text().toAbsolutePath()).append('\t')
                    .append(reading.wav().toAbsolutePath()).append('\n'));
            write(list, lines.toString());
            final List<String> full = new ArrayList<>(command);
            full.add("--list");
            full.add(list.toAbsolutePath().toString());
            ExternalCommand.run(full, Path.of("").toAbsolutePath());
        } else {
            for (final Reading reading : all) {
                final List<String> full = new ArrayList<>(command);
                full.add(reading.text().toAbsolutePath().toString());
                full.add(reading.wav().toAbsolutePath().toString());
                ExternalCommand.run(full, Path.of("").toAbsolutePath());
            }
        }
        return pieces.stream().map(Speech::joined).toList();
    }

    /*
     * The parts the voice read, each with its gaps between sentences shortened, joined with the pauses' silence and
     * written as the clip's WAV.
     */
    private WavFile joined() {
        final List<WavFile> spoken = readings.stream()
                .map(reading -> WavFile.read(reading.wav()).withShorterGaps(LONGEST_GAP)).toList();
        final WavFile format = spoken.getFirst();
        if (spoken.stream().anyMatch(part -> part.sampleRate() != format.sampleRate()
                || part.channels() != format.channels())) {
            throw new IllegalStateException("the voice read the parts of " + clip.getFileName()
                    + " in different formats; each part must have the first part's rate and channels");
        }
        final ByteArrayOutputStream joined = new ByteArrayOutputStream();
        for (int i = 0; i < parts.size(); i++) {
            joined.writeBytes(spoken.get(i).samples());
            joined.writeBytes(spoken.get(i).silence(parts.get(i).pauseAfter()).samples());
        }
        final WavFile whole = new WavFile(format.sampleRate(), format.channels(), joined.toByteArray());
        whole.write(clip.resolveSibling(clip.getFileName() + ".wav"));
        return whole;
    }

    private static void write(final Path file, final String text) {
        try {
            Files.writeString(file, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
