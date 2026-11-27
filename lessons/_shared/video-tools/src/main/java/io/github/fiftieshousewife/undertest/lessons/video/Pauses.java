package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Pause markers in narration, such as {@code [pause 1s]} or {@code [pause 500ms]}: the voice stops for that long.
 * Slides and speaker notes leave the markers out. {@code WORDS_PER_SECOND} is how fast the voice speaks.
 */
final class Pauses {

    static final double WORDS_PER_SECOND = 2.6;

    private static final Pattern MARKER = Pattern.compile("\\s*\\[pause (\\d+(?:\\.\\d+)?)(ms|s)]\\s*");

    /* A piece of narration to speak, and the silence that follows it, in seconds. */
    record Part(String text, double pauseAfter) {
    }

    private Pauses() {
    }

    /* The narration split at its pause markers. */
    static List<Part> parts(final String narration) {
        final List<Part> parts = new ArrayList<>();
        final Matcher marker = MARKER.matcher(narration);
        int start = 0;
        while (marker.find()) {
            parts.add(new Part(narration.substring(start, marker.start()).strip(),
                    seconds(marker.group(1), marker.group(2))));
            start = marker.end();
        }
        parts.add(new Part(narration.substring(start).strip(), 0));
        return parts.stream().filter(part -> !part.text().isEmpty() || part.pauseAfter() > 0).toList();
    }

    /* The narration without its pause markers, as a slide shows it. */
    static String withoutMarkers(final String narration) {
        return MARKER.matcher(narration).replaceAll(" ").strip();
    }

    /* Seconds the voice takes to say a narration: its words at the voice's speed, and its pauses. */
    static double spokenSeconds(final String narration) {
        double pauses = 0;
        for (final Part part : parts(narration)) {
            pauses += part.pauseAfter();
        }
        final String words = withoutMarkers(narration).strip();
        return (words.isEmpty() ? 0 : words.split("\\s+").length / WORDS_PER_SECOND) + pauses;
    }

    private static double seconds(final String amount, final String unit) {
        final double value = Double.parseDouble(amount);
        return "ms".equals(unit) ? value / 1000 : value;
    }
}
