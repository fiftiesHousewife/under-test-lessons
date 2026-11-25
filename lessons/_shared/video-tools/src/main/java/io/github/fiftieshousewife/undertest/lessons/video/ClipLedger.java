package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * Who or what made each of a deck's voice clips, kept beside them in {@code voices.txt}: one line per clip, its name
 * then {@code voice <key> <engine>} for a clip the machine voice read (the key of what it was given, and the voice
 * engine: its program, voice, speed, model and packages), or {@code person <name>} for a person's recording. A clip
 * with no line was also put there by a person. A person's clip is never read again or removed; the voice's is read
 * again when its words or its engine change.
 */
final class ClipLedger {

    static final String FILE = "voices.txt";
    private static final String VOICE = "voice ";
    private static final String PERSON = "person ";
    private static final String HEADING = "# Who or what made each voice clip: \"voice <key> <engine>\" is the machine"
            + " voice, from what it was given; \"person <name>\", or no line, a person's recording.\n";

    private final Path file;
    private final Map<String, String> madeBy;
    private final boolean firstLedger;

    private ClipLedger(final Path file, final Map<String, String> madeBy, final boolean first) {
        this.file = file;
        this.madeBy = madeBy;
        this.firstLedger = first;
    }

    static ClipLedger read(final Path folder) {
        final Path file = folder.resolve(FILE);
        final Map<String, String> madeBy = new TreeMap<>();
        if (Files.isRegularFile(file)) {
            lines(file).forEach(line -> readLine(line, madeBy));
        }
        return new ClipLedger(file, madeBy, !Files.isRegularFile(file));
    }

    /* A line {@code <clip> <who made it>}; comments and lines without a space are skipped. */
    private static void readLine(final String line, final Map<String, String> madeBy) {
        final int space = line.indexOf(' ');
        if (space > 0 && !line.startsWith("#")) {
            madeBy.put(line.substring(0, space), line.substring(space + 1).strip());
        }
    }

    /*
     * Whether the machine voice read this clip from exactly what it would be given now, with this engine. A line from
     * before engines were recorded counts as this engine's.
     */
    boolean readFrom(final String clip, final String saidKey, final String engine) {
        final String by = madeBy.getOrDefault(clip, "");
        return by.equals(VOICE + saidKey) || by.equals(VOICE + saidKey + " " + engine);
    }

    /* Records the engine on a line from before engines were recorded, which was this engine's. */
    void stampEngine(final String clip, final String engine) {
        final String by = madeBy.getOrDefault(clip, "");
        if (by.startsWith(VOICE) && by.indexOf(' ', VOICE.length()) < 0) {
            madeBy.put(clip, by + " " + engine);
        }
    }

    /*
     * Who made each of these clips, for a deck's mix of voices: the machine voice's engine ("unrecorded" on a line from
     * before engines were), a person's name, or "a person" for a clip with no line.
     */
    Map<String, Integer> mix(final List<String> clips) {
        final Map<String, Integer> mix = new TreeMap<>();
        clips.forEach(clip -> mix.merge(who(madeBy.getOrDefault(clip, "")), 1, Integer::sum));
        return mix;
    }

    private static String who(final String by) {
        if (by.startsWith(VOICE)) {
            final int space = by.indexOf(' ', VOICE.length());
            return space < 0 ? "the voice (engine unrecorded)" : "the voice " + by.substring(space + 1);
        }
        return by.startsWith(PERSON) ? by.substring(PERSON.length()).strip() : "a person";
    }

    /* Whether the machine voice made this clip: true unless a person put it there. */
    boolean byTheVoice(final String clip) {
        return madeBy.getOrDefault(clip, "").startsWith(VOICE);
    }

    /*
     * Whether this is the folder's first ledger: its clips came before ledgers, when the machine voice made every
     * clip, so they are the voice's.
     */
    boolean first() {
        return firstLedger;
    }

    void voiceRead(final String clip, final String saidKey, final String engine) {
        madeBy.put(clip, VOICE + saidKey + " " + engine);
    }

    void forget(final String clip) {
        madeBy.remove(clip);
    }

    void write() {
        final StringBuilder text = new StringBuilder(HEADING);
        madeBy.forEach((clip, by) -> text.append(clip).append(' ').append(by).append('\n'));
        try {
            Files.writeString(file, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static java.util.List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
