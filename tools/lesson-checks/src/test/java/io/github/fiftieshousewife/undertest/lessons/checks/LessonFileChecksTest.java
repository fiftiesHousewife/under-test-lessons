package io.github.fiftieshousewife.undertest.lessons.checks;

import static io.github.fiftieshousewife.undertest.lessons.checks.TestRepo.lessonLink;
import static io.github.fiftieshousewife.undertest.lessons.checks.TestRepo.techniqueLink;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LessonFileChecksTest {

    @TempDir
    Path root;

    private Path toolkit;

    @BeforeEach
    void writeAnAgreeingRepo() {
        final TestRepo repo = new TestRepo(root).lesson("feature-flags", "feature-flags")
                .catalogue("feature-flags|" + lessonLink("feature-flags"))
                .index(lessonLink("feature-flags") + "|" + techniqueLink("feature-flags"));
        toolkit = repo.toolkit("feature-flags");
        TestRepo.write(root, "README.md", "See " + techniqueLink("feature-flags") + ".\n");
    }

    @Test
    void passesWhenEverythingAgrees() {
        assertAll(
                () -> assertThatCode(() -> LessonFileChecks.main(root.toString(), toolkit.toString()))
                        .doesNotThrowAnyException(),
                () -> assertThatCode(() -> LessonFileChecks.main(root.toString())).doesNotThrowAnyException());
    }

    @Test
    void reportsTechniquesAndLinksTheToolkitDoesNotHave() {
        TestRepo.write(root, "lessons/feature-flags/lesson.yaml",
                "id: feature-flags\ntitle: Flags\ntechniques: [flags]\n");
        TestRepo.write(root, "docs/research/old-review.md", techniqueLink("snapshot-testing"));
        TestRepo.write(root, "docs/plan.md", techniqueLink("snapshot-testing"));

        final List<String> messages = LessonFileChecks.check(root, Optional.of(Set.of("feature-flags"))).stream()
                .map(Problem::message).toList();

        assertAll(
                () -> assertThat(messages).contains("names the technique flags, which the toolkit doesn't have"),
                () -> assertThat(messages).filteredOn(message -> message.contains("snapshot-testing")).hasSize(1));
    }

    @Test
    void aLessonWithAnExampleMustTeachATechniqueAndAMapNeedNot() {
        TestRepo.write(root, "lessons/feature-flags/lesson.yaml", "id: feature-flags\ntitle: Flags\ntechniques: []\n");
        final List<String> withoutExample = LessonFileChecks.check(root, Optional.empty()).stream()
                .map(Problem::message).toList();
        TestRepo.write(root, "lessons/feature-flags/example/build.gradle.kts", "plugins { }\n");

        final List<String> withExample = LessonFileChecks.check(root, Optional.empty()).stream()
                .map(Problem::message).toList();

        assertAll(
                () -> assertThat(withoutExample).noneMatch(message -> message.contains("names no toolkit technique")),
                () -> assertThat(withExample).anyMatch(message -> message.contains("names no toolkit technique")));
    }

    @Test
    void aLessonWithAnExampleMustHaveACheckedExercise() {
        TestRepo.write(root, "lessons/feature-flags/example/build.gradle.kts", "plugins { }\n");
        final List<String> without = messages();
        TestRepo.write(root, "lessons/feature-flags/example/src/test/java/demo/answer/DemoAnswerTest.java",
                "class X { }\n");
        TestRepo.write(root, "lessons/feature-flags/README.md", "# Flags\n\n## Exercise\n\nTry it.\n");

        final List<String> with = messages();

        assertAll(
                () -> assertThat(without).contains("has no answer package for its exercise",
                        "has no \"## Exercise\" section"),
                () -> assertThat(with).noneMatch(message -> message.contains("exercise")
                        || message.contains("Exercise")));
    }

    @Test
    void aLessonMayTeachAJourneyStepByItsSkillAndTheSkillMustExist() {
        TestRepo.write(root, "lessons/feature-flags/lesson.yaml",
                "id: feature-flags\ntitle: Flags\ntechniques: []\nskills: [draft-the-connection-list]\n");
        TestRepo.write(root, "lessons/feature-flags/example/build.gradle.kts", "plugins { }\n");
        TestRepo.write(toolkit, ".github/skills/draft-the-connection-list/SKILL.md", "# Draft\n");
        final List<String> known = LessonFileChecks.unknownSkills(root, new Toolkit(toolkit).skillNames()).stream()
                .map(Problem::message).toList();
        final List<String> unknown = LessonFileChecks.unknownSkills(root, Set.of()).stream()
                .map(Problem::message).toList();

        assertAll(
                () -> assertThat(known).isEmpty(),
                () -> assertThat(unknown).containsExactly(
                        "names the skill draft-the-connection-list, which the toolkit's .github/skills/ doesn't have"),
                () -> assertThat(messages()).noneMatch(message -> message.contains("names no toolkit technique")));
    }

    @Test
    void failsTheBuildOnAnyProblem() {
        TestRepo.write(root, "lessons/new-lesson/README.md", "# New\n");

        assertThatThrownBy(() -> LessonFileChecks.main(root.toString(), toolkit.toString()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void refusesWrongArgumentsAndAFolderThatIsNotTheToolkit() {
        final String notTheToolkit = root.resolve("lessons").toString();

        assertAll(
                () -> assertThatThrownBy(LessonFileChecks::main).isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> LessonFileChecks.main(root.toString(), notTheToolkit))
                        .hasMessageContaining("not a toolkit checkout"));
    }

    private List<String> messages() {
        return LessonFileChecks.check(root, Optional.empty()).stream().map(Problem::message).toList();
    }
}
