package io.github.fiftieshousewife.undertest.lessons.checks;

import java.util.List;

/** One lesson's {@code lesson.yaml}: its folder name, its title, and the toolkit techniques it teaches. */
public record Lesson(String id, String title, List<String> techniques) {

    public Lesson {
        techniques = List.copyOf(techniques);
    }
}
