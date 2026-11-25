package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/*
 * A piece of narration as written, and as the voice is given it: with the pronunciation list applied, and its pause
 * markers. Its clip is named by the written words alone, so a pronunciation fix or a reflowed paragraph keeps the
 * clip's name, and a person's recording of it is never lost; what the voice was given decides whether a machine
 * voice must read it again. {@code MADE} says how the voice's sound is made into a clip: its gaps between sentences
 * cut to 0.4 s (in video-render). Changing how clips are made changes it, so every clip the voice read is read again
 * once.
 */
record Spoken(String written, String said) {

    private static final int NAME_LENGTH = 16;
    private static final String MADE = "gaps 0.4\n";

    static Spoken of(final String written, final Pronunciation pronunciation) {
        return new Spoken(written, pronunciation.forSpeech(written));
    }

    /*
     * The clip's file name as the voice makes it: the start of the SHA-256 of the written words, pause markers and
     * spacing left out, as {@code .m4a}. A person's recording may have the same name as {@code .wav} or {@code .mp3}.
     */
    String clipName() {
        return clipStem() + ".m4a";
    }

    /* The clip's file name without its format. */
    String clipStem() {
        return hash(Pauses.withoutMarkers(written).replaceAll("\\s+", " "));
    }

    /*
     * What the voice was given, and how its clip was made, as a short hash: it changes with a pronunciation fix or a
     * pause, the name doesn't.
     */
    String saidKey() {
        return hash(MADE + said);
    }

    /* The name clips had before they were named by their written words: the hash of what the voice was given. */
    String earlierClipName() {
        return hash(said) + ".m4a";
    }

    private static String hash(final String text) {
        try {
            final byte[] digest = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest).substring(0, NAME_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("every Java has SHA-256", e);
        }
    }
}
