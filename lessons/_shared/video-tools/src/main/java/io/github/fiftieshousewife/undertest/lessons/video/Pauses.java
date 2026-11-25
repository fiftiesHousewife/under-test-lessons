package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Pause markers in narration, such as {@code [pause 1s]} or {@code [pause 500ms]}: the voice stops for that long.
 * Captions leave the markers out.
 */
final class Pauses {

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
                    VhsTime.seconds(marker.group(1), marker.group(2))));
            start = marker.end();
        }
        parts.add(new Part(narration.substring(start).strip(), 0));
        return parts.stream().filter(part -> !part.text().isEmpty() || part.pauseAfter() > 0).toList();
    }

    /* The narration without its pause markers, for captions. */
    static String withoutMarkers(final String narration) {
        return MARKER.matcher(narration).replaceAll(" ").strip();
    }
}
