package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Turns scenes and their narration into caption cues, and writes them as WebVTT or SRT. Each scene's narration is split
 * into short cues at sentence ends (and at spaces, for long sentences); each cue shows for a share of its scene in
 * proportion to its length.
 */
public final class CaptionFile {

    static final int MAX_CUE_CHARACTERS = 90;
    static final int MAX_LINE_CHARACTERS = 48;

    private CaptionFile() {
    }

    /** Cues for every scene. There must be one narration paragraph per scene. */
    public static List<Cue> cues(final List<Scene> scenes, final List<String> narration) {
        if (scenes.size() != narration.size()) {
            throw new IllegalArgumentException(scenes.size() + " scenes but " + narration.size()
                    + " narration paragraphs; there must be one paragraph per scene");
        }
        final List<Cue> cues = new ArrayList<>();
        for (int i = 0; i < scenes.size(); i++) {
            cues.addAll(cuesFor(scenes.get(i), narration.get(i)));
        }
        return cues;
    }

    static List<Cue> cuesFor(final Scene scene, final String text) {
        final List<String> chunks = chunks(Pauses.withoutMarkers(text));
        final double total = chunks.stream().mapToInt(String::length).sum();
        final List<Cue> cues = new ArrayList<>();
        double start = scene.start();
        for (final String chunk : chunks) {
            final double end = start + (scene.end() - scene.start()) * chunk.length() / total;
            cues.add(new Cue(start, end, wrap(chunk)));
            start = end;
        }
        return cues;
    }

    static List<String> chunks(final String text) {
        final List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (final String word : text.split(" ")) {
            if (!current.isEmpty() && current.length() + word.length() + 1 > MAX_CUE_CHARACTERS) {
                chunks.add(current.toString());
                current = new StringBuilder();
            }
            current.append(current.isEmpty() ? "" : " ").append(word);
            if (word.matches(".*[.!?:]$") && current.length() > MAX_CUE_CHARACTERS / 2) {
                chunks.add(current.toString());
                current = new StringBuilder();
            }
        }
        if (!current.isEmpty()) {
            chunks.add(current.toString());
        }
        return chunks;
    }

    /** Breaks a cue into two lines at the space nearest the middle, if it is too long for one. */
    static String wrap(final String cue) {
        if (cue.length() <= MAX_LINE_CHARACTERS) {
            return cue;
        }
        final int middle = cue.length() / 2;
        final int before = cue.lastIndexOf(' ', middle);
        final int after = cue.indexOf(' ', middle);
        final int at = after < 0 || before >= 0 && middle - before <= after - middle ? before : after;
        return at < 0 ? cue : cue.substring(0, at) + "\n" + cue.substring(at + 1);
    }

    public static String webVtt(final List<Cue> cues) {
        final StringBuilder out = new StringBuilder("WEBVTT\n\n");
        for (final Cue cue : cues) {
            out.append(time(cue.start(), '.')).append(" --> ").append(time(cue.end(), '.')).append('\n')
                    .append(cue.text()).append("\n\n");
        }
        return out.toString();
    }

    public static String srt(final List<Cue> cues) {
        final StringBuilder out = new StringBuilder();
        for (int i = 0; i < cues.size(); i++) {
            final Cue cue = cues.get(i);
            out.append(i + 1).append('\n').append(time(cue.start(), ',')).append(" --> ").append(time(cue.end(), ','))
                    .append('\n').append(cue.text()).append("\n\n");
        }
        return out.toString();
    }

    static String time(final double seconds, final char separator) {
        final long millis = Math.round(seconds * 1000);
        return String.format(Locale.ROOT, "%02d:%02d:%02d%c%03d",
                millis / 3_600_000, millis / 60_000 % 60, millis / 1000 % 60, separator, millis % 1000);
    }
}
