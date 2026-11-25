package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlidesTest {

    @TempDir
    Path folder;

    private static final String SLIDES = """
                # Slides

                ## intro: What is mutation testing?
                - Plant a small bug
                - See if a test fails
                Say: Mutation testing plants small bugs,
                  one at a time.

                ## closing: Find out more
                - https://pitest.org
                Picture: globe
                Say: Find out more at the PIT website.
                """;

    @Test
    void readsIntroAndClosingSlides() throws IOException {
        final Path file = Files.writeString(folder.resolve("x.slides.md"), SLIDES);
        final Slides slides = Slides.read(file, folder);

        assertAll(
                () -> assertThat(slides.intro()).containsExactly(new Slide(
                        new Slide.Heading("What is mutation testing?", ""),
                        List.of("Plant a small bug", "See if a test fails"),
                        "Mutation testing plants small bugs, one at a time.")),
                () -> assertThat(slides.closing().getFirst().points()).containsExactly("https://pitest.org"),
                () -> assertThat(slides.closing().getFirst().heading().picture()).isEqualTo("globe"),
                () -> assertThat(Slides.read(folder.resolve("missing.md"), folder).intro()).isEmpty());
    }

    @Test
    void rejectsSlidesItCannotUse() throws IOException {
        final Path badKind = Files.writeString(folder.resolve("a.md"), "## middle: x\nSay: y\n");
        final Path noNarration = Files.writeString(folder.resolve("b.md"), "## intro: x\n- point\n");

        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Slides.read(badKind, folder))
                        .withMessageContaining("intro: or closing:"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Slides.read(noNarration, folder))
                        .withMessageContaining("has no Say: line"));
    }

    @Test
    void bringsInSharedSlides() throws IOException {
        Files.writeString(folder.resolve("how-we-build.md"),
                "# Shared\n\n## intro: How we build\n- Gradle\nSay: We use Gradle.\n");
        final Path file = Files.writeString(folder.resolve("x.md"),
                "## intro: First\nSay: One.\nInclude: how-we-build\n");
        final Path missing = Files.writeString(folder.resolve("y.md"), "Include: nowhere\n");

        assertAll(
                () -> assertThat(Slides.read(file, folder).intro()).extracting(Slide::title)
                        .containsExactly("First", "How we build"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Slides.read(missing, folder))
                        .withMessageContaining("no shared slides file"));
    }
}
