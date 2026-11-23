package io.github.fiftieshousewife.undertest.lessons.checks;

import java.util.List;

/**
 * One lesson's {@code lesson.yaml}: its folder name, its title, and what of the toolkit it teaches: techniques, and
 * the steps of the toolkit's journey, by their skills' names.
 */
public record Lesson(String id, String title, Teaches teaches) {

    /** The toolkit's techniques a lesson teaches, and the skills of the journey steps it teaches. */
    public record Teaches(List<String> techniques, List<String> skills) {

        public Teaches {
            techniques = List.copyOf(techniques);
            skills = List.copyOf(skills);
        }

        boolean nothing() {
            return techniques.isEmpty() && skills.isEmpty();
        }
    }

    /** A lesson that teaches techniques only. */
    public Lesson(final String id, final String title, final List<String> techniques) {
        this(id, title, new Teaches(techniques, List.of()));
    }

    public List<String> techniques() {
        return teaches.techniques();
    }

    public List<String> skills() {
        return teaches.skills();
    }
}
