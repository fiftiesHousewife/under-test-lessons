package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlidesTest {

    @TempDir
    Path folder;

    @Test
    void readsIntroAndClosingSlides() throws IOException {
        final Path file = Files.writeString(folder.resolve("x.slides.md"), """
                # Slides

                ## intro: What is mutation testing?
                - Plant a small bug
                - See if a test fails
                Say: Mutation testing plants small bugs,
                  one at a time.

                ## closing: Find out more
                - https://pitest.org
                Say: Find out more at the PIT website.
                """);
        final Slides slides = Slides.read(file);

        assertAll(
                () -> assertThat(slides.intro()).containsExactly(new Slide("What is mutation testing?",
                        List.of("Plant a small bug", "See if a test fails"),
                        "Mutation testing plants small bugs, one at a time.")),
                () -> assertThat(slides.closing().getFirst().points()).containsExactly("https://pitest.org"),
                () -> assertThat(Slides.read(folder.resolve("missing.md")).intro()).isEmpty());
    }

    @Test
    void rejectsSlidesItCannotUse() throws IOException {
        final Path badKind = Files.writeString(folder.resolve("a.md"), "## middle: x\nSay: y\n");
        final Path noNarration = Files.writeString(folder.resolve("b.md"), "## intro: x\n- point\n");

        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Slides.read(badKind))
                        .withMessageContaining("intro: or closing:"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Slides.read(noNarration))
                        .withMessageContaining("has no Say: line"));
    }

    @Test
    void drawsAFullHdSlideWithWrappedPointsAndLinks() throws IOException {
        final Path png = folder.resolve("slide.png");
        SlideImage.draw(new Slide("Why coverage is not enough", List.of("A test can run a line without checking"
                + " anything about what it produced, and still count it as covered, which this long point shows.",
                "https://pitest.org"), "narration"), png);
        final BufferedImage image = ImageIO.read(png.toFile());

        assertAll(
                () -> assertThat(image.getWidth()).isEqualTo(1920),
                () -> assertThat(image.getHeight()).isEqualTo(1080));
    }
}
