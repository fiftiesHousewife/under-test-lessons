package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NarrationTest {

    @TempDir
    Path folder;

    @Test
    void readsOnePlainParagraphPerNumberedItem() throws IOException {
        final Path file = folder.resolve("x.narration.md");
        Files.writeString(file, """
                # Narration

                Intro text that is not a scene.

                1. **Why.** Coverage counts lines,
                   not checks.
                2. Run `./gradlew pitest`.
                """);

        assertThat(Narration.paragraphs(file))
                .containsExactly("Why. Coverage counts lines, not checks.", "Run ./gradlew pitest.");
    }
}
