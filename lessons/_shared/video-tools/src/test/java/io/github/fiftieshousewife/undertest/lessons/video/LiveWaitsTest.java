package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* A live run's wait must name a line its kept output shows, or the recording stalls. */
class LiveWaitsTest {

    @TempDir
    Path root;

    private Media lesson(final String firstWait, final String secondWait) throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.write(media.resolve("demo.tape"), List.of(
                "Type@1ms `run_live \"./gradlew test -Pcode=today\"`", "Enter", firstWait,
                "Type@1ms `run_live \"./gradlew test\"`", "Enter", secondWait));
        final Path outputs = Files.createDirectories(media.resolve("demo.outputs"));
        Files.write(outputs.resolve("01.txt"), List.of("$ ./gradlew test -Pcode=today", "4 tests completed, 1 failed"));
        Files.write(outputs.resolve("02.txt"), List.of("$ ./gradlew test", "All 4 tests passed."));
        return new Media(root, "demo");
    }

    @Test
    void namesTheWaitThatItsRunNoLongerPrints() throws IOException {
        final Media demo = lesson("Wait+Screen@300s /tests failed\\. Report/", "Wait+Screen@300s /tests? passed/");

        assertThat(LiveWaits.problems(demo)).singleElement().extracting(Problem::message).asString()
                .contains("/tests failed\\. Report/", "01.txt");
    }

    @Test
    void acceptsWaitsTheRunsPrintAndTheBuildFailedLine() throws IOException {
        final Media demo = lesson("Wait+Screen@300s /completed, \\d+ failed/", "Wait+Screen@300s /BUILD FAILED in/");

        assertThat(LiveWaits.problems(demo)).isEmpty();
    }
}
