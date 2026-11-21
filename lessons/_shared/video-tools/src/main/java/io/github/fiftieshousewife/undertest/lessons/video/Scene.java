package io.github.fiftieshousewife.undertest.lessons.video;

/** One scene of a lesson video, and when it is on screen, in seconds from the start of the video. */
public record Scene(String title, double start, double end) {
}
