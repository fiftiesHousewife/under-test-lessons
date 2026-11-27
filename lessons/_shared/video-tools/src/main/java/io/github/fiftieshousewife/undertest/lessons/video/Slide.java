package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.List;

/**
 * One slide: its heading (title, and the picture beside the points), the points shown, and the narration spoken.
 */
public record Slide(Heading heading, List<String> points, String narration) {

    /** A slide's title, and the name of its picture (empty for none). */
    public record Heading(String title, String picture) {
    }

    /** Keeps its own copy of the points. */
    public Slide {
        points = List.copyOf(points);
    }

    public String title() {
        return heading.title();
    }
}
