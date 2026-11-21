package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.List;

/** One slide: a title, a few short points shown on screen, and the narration spoken over it. */
public record Slide(String title, List<String> points, String narration) {

    /** Keeps its own copy of the points. */
    public Slide {
        points = List.copyOf(points);
    }
}
