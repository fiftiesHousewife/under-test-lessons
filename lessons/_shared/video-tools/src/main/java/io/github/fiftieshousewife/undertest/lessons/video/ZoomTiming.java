package io.github.fiftieshousewife.undertest.lessons.video;

/**
 * When a zoom on key code happens, worked out from the video script alone: the timings the checks and the recording
 * share. Drawing the zoom into the recorded frames is {@code Zoom}'s job, in production.
 */
final class ZoomTiming {

    /** Seconds after the code appears before the zoom starts, and before the scene ends that it is back out. */
    static final double DELAY = 1.0;
    /** Seconds the zoom takes to ease in, and to ease out. */
    static final double EASE = 0.8;
    /** The shortest hold worth zooming for, in seconds. */
    static final double SHORTEST_HOLD = 1.0;

    private ZoomTiming() {
    }

    /** When a zoom's code is on screen, in seconds of video: from when it appears to the end of its scene. */
    record Span(double start, double end) {

        /** How far in the zoom is at a moment: 0 (not at all) to 1 (fully), eased at both ends. */
        double amount(final double time) {
            if (end - start < 2 * (DELAY + EASE) + SHORTEST_HOLD) {
                return 0;
            }
            final double in = (time - start - DELAY) / EASE;
            final double out = (end - DELAY - time) / EASE;
            final double progress = Math.max(0, Math.min(1, Math.min(in, out)));
            return (1 - Math.cos(Math.PI * progress)) / 2;
        }

        Span scaled(final double factor) {
            return new Span(start * factor, end * factor);
        }
    }
}
