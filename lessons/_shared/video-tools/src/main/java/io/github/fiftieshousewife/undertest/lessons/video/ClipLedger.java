package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * Who or what made each of a deck's voice clips, kept beside them in {@code voices.txt}: one line per clip, its name
 * then {@code voice <key>} for a clip the machine voice read (the key of what it was given). A clip with no line was
 * put there by a person: it is never read again or removed.
 */
final class ClipLedger {

    static final String FILE = "voices.txt";
    private static final String VOICE = "voice ";
    private static final String HEADING = "# Who or what made each voice clip: \"voice <key>\" is the machine voice,"
            + " from what it was given; a clip not listed was recorded by a person.\n";

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
            for (final String line : lines(file)) {
                final int space = line.indexOf(' ');
                if (space > 0 && !line.startsWith("#")) {
                    madeBy.put(line.substring(0, space), line.substring(space + 1).strip());
                }
            }
        }
        return new ClipLedger(file, madeBy, !Files.isRegularFile(file));
    }

    /** Whether the machine voice read this clip from exactly what it would be given now. */
    boolean readFrom(final String clip, final String saidKey) {
        return (VOICE + saidKey).equals(madeBy.get(clip));
    }

    /** Whether the machine voice made this clip: true unless a person put it there. */
    boolean byTheVoice(final String clip) {
        return madeBy.getOrDefault(clip, "").startsWith(VOICE);
    }

    /**
     * Whether this is the folder's first ledger: its clips came before ledgers, when the machine voice made every
     * clip, so they are the voice's.
     */
    boolean first() {
        return firstLedger;
    }

    void voiceRead(final String clip, final String saidKey) {
        madeBy.put(clip, VOICE + saidKey);
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
