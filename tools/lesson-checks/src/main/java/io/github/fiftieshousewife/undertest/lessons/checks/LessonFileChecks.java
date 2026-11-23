package io.github.fiftieshousewife.undertest.lessons.checks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point: {@code <repository root> [<toolkit checkout>]}. Fails if the lesson.yaml files, the catalogue, the
 * lessons index and (when a toolkit checkout is given) the toolkit's techniques and journey steps disagree, or if a
 * lesson with an example teaches neither a toolkit technique nor a journey step.
 */
@Slf4j
public final class LessonFileChecks {

    private LessonFileChecks() {
    }

    public static void main(final String... args) {
        if (args.length == 0 || args.length > 2) {
            throw new IllegalArgumentException("usage: LessonFileChecks <repository root> [<toolkit checkout>]");
        }
        final Optional<Toolkit> toolkit = args.length == 2
                ? Optional.of(new Toolkit(Path.of(args[1])))
                : Optional.empty();
        if (toolkit.isEmpty()) {
            log.warn("No toolkit checkout found, so lessons were not checked against the toolkit's techniques."
                    + " Put under-test beside this repository, or pass -PtoolkitDir=<path>.");
        }
        final Path root = Path.of(args[0]);
        final List<Problem> problems = new ArrayList<>(check(root, toolkit.map(Toolkit::techniqueIds)));
        toolkit.ifPresent(checkout -> problems.addAll(unknownSkills(root, checkout.skillNames())));
        failOn(problems);
    }

    static List<Problem> check(final Path root, final Optional<Set<String>> techniques) {
        final List<Problem> problems = new ArrayList<>();
        final List<Lesson> lessons = new LessonReader().read(root, problems);
        problems.addAll(LessonTables.catalogue(root, lessons, techniques));
        problems.addAll(LessonTables.index(root, lessons));
        problems.addAll(lessonsWithoutATechnique(root, lessons));
        problems.addAll(lessonsWithoutAnExercise(root, lessons));
        techniques.ifPresent(ids -> {
            problems.addAll(unknownTechniques(root, lessons, ids));
            problems.addAll(TechniqueLinks.check(root, ids));
        });
        return problems;
    }

    /**
     * Every lesson with an example teaches at least one of the toolkit's techniques, so the two repositories match
     * both ways. A lesson without an example, such as the overview, which maps the others, needn't name one.
     */
    private static List<Problem> lessonsWithoutATechnique(final Path root, final List<Lesson> lessons) {
        final List<Problem> problems = new ArrayList<>();
        for (final Lesson lesson : lessons) {
            final Path folder = root.resolve("lessons/" + lesson.id());
            if (lesson.teaches().nothing() && Files.isDirectory(folder.resolve("example"))) {
                problems.add(new Problem(folder.resolve("lesson.yaml"), "names no toolkit technique or journey step:"
                        + " name what it teaches in techniques or skills, or ask for it to be added to the toolkit"));
            }
        }
        return problems;
    }

    /**
     * Every lesson with an example has an exercise whose answer the build checks: an {@code answer} package in its
     * example, and an "Exercise" section in its README (docs/decisions/0029). Its README also says what to use on
     * older Java (docs/decisions/0028).
     */
    private static List<Problem> lessonsWithoutAnExercise(final Path root, final List<Lesson> lessons) {
        final List<Problem> problems = new ArrayList<>();
        for (final Lesson lesson : lessons) {
            final Path folder = root.resolve("lessons/" + lesson.id());
            if (Files.isDirectory(folder.resolve("example"))) {
                if (!hasAnswerPackage(folder.resolve("example"))) {
                    problems.add(new Problem(folder.resolve("example"), "has no answer package for its exercise"));
                }
                problems.addAll(missingSections(folder.resolve("README.md")));
            }
        }
        return problems;
    }

    /** The sections every lesson's README with an example must have. */
    private static final List<String> SECTIONS = List.of("## Exercise", "## On older Java");

    private static List<Problem> missingSections(final Path readme) {
        final List<String> lines = Files.isRegularFile(readme) ? Text.read(readme).lines().toList() : List.of();
        return SECTIONS.stream().filter(section -> !lines.contains(section))
                .map(section -> new Problem(readme, "has no \"" + section + "\" section")).toList();
    }

    private static boolean hasAnswerPackage(final Path example) {
        try (java.util.stream.Stream<Path> paths = Files.walk(example)) {
            return paths.anyMatch(path -> Files.isDirectory(path) && "answer".equals(String.valueOf(path.getFileName()))
                    && !path.toString().contains("/build/"));
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

    /** Every skill a lesson names is one of the toolkit's journey steps: a folder under its .github/skills/. */
    static List<Problem> unknownSkills(final Path root, final Set<String> skills) {
        final List<Problem> problems = new ArrayList<>();
        for (final Lesson lesson : new LessonReader().read(root, new ArrayList<>())) {
            lesson.skills().stream().filter(skill -> !skills.contains(skill))
                    .forEach(skill -> problems.add(new Problem(root.resolve("lessons/" + lesson.id() + "/lesson.yaml"),
                            "names the skill " + skill + ", which the toolkit's .github/skills/ doesn't have")));
        }
        return problems;
    }

    private static List<Problem> unknownTechniques(final Path root, final List<Lesson> lessons, final Set<String> ids) {
        final List<Problem> problems = new ArrayList<>();
        for (final Lesson lesson : lessons) {
            final Path file = root.resolve("lessons/" + lesson.id() + "/lesson.yaml");
            lesson.techniques().stream().filter(technique -> !ids.contains(technique))
                    .forEach(technique -> problems.add(new Problem(file,
                            "names the technique " + technique + ", which the toolkit doesn't have")));
        }
        return problems;
    }

    private static void failOn(final List<Problem> problems) {
        problems.forEach(problem -> log.error("{}", problem.toString().replace('\r', ' ').replace('\n', ' ')));
        if (!problems.isEmpty()) {
            throw new IllegalStateException(problems.size() + " problem(s) between the lessons and the toolkit");
        }
        log.info("Lessons and the toolkit agree: lesson.yaml files, catalogue, lessons index and technique links.");
    }
}
