package io.github.fiftieshousewife.undertest.lessons.video;

/** One caption: its text and when it shows, in seconds from the start of the video. */
public record Cue(double start, double end, String text) {
}
