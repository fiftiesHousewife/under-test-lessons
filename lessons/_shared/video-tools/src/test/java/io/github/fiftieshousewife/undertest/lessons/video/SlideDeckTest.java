package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlideDeckTest {

    @TempDir
    Path root;

    @AfterEach
    void restore() {
        System.clearProperty("video.browser");
    }

    private Path writeALesson() throws IOException {
        final Path shared = Files.createDirectories(root.resolve("lessons/_shared/slides/pictures"));
        Files.writeString(shared.resolveSibling("slides.css"), "h1 { color: teal; }");
        Files.writeString(shared.resolveSibling("deck.css"), ".deck-slide { }");
        Files.writeString(shared.resolveSibling("deck.js"), "// controls");
        Files.writeString(shared.resolve("terminal.svg"), "<svg id=\"terminal\"></svg>");
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.writeString(media.resolveSibling("README.md"), "# Demo lesson\n\nText.\n");
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\nSay: Hi [pause 1s] there.\n"
                + "## closing: Bye\nSay: Bye.\n");
        Files.writeString(media.resolveSibling("Thing.java"), "class Thing {\n    int size() { return 1; }\n}\n");
        final Path other = Files.createDirectories(root.resolve("lessons/another/media"));
        Files.writeString(other.resolveSibling("Thing.java"), "class SomeoneElsesThing {\n}\n");
        writeTheScenes(media);
        return media;
    }

    private static void writeTheScenes(final Path media) throws IOException {
        Files.writeString(media.resolve("demo.scenes"), "# The scenes of the demo deck.\n\n"
                + "## Scene 1: the code under test\n"
                + "note \"The code under test: the thing & its *size*.\"\n"
                + "show Thing 1 2 \"the thing\"\nrun_live \"./gradlew test\"\n"
                + "callout \"It passes.\"\n");
        Files.writeString(media.resolve("demo.narration.md"), "1. Here is the code & its tests.\n");
    }

    /* A browser that only "prints" a PDF, by making an empty file where asked. */
    private void useAStandInBrowser() throws IOException {
        final Path browser = Files.writeString(root.resolve("browser"), "#!/bin/sh\nfor a; do case \"$a\" in"
                + " --print-to-pdf=*) touch \"${a#--print-to-pdf=}\";; esac; done\n");
        if (!browser.toFile().setExecutable(true)) {
            throw new IOException("can't make the stand-in browser executable");
        }
        System.setProperty("video.browser", browser.toString());
    }

    @Test
    void writesADeckWithIntroSceneAndClosingSlidesAndItsNotes() throws IOException {
        final Path media = writeALesson();
        useAStandInBrowser();

        LessonDecks.main("deck", root.toString(), "all", "pdf");
        final String deck = Files.readString(media.resolve("demo.deck.html"));

        assertAll(
                () -> assertThat(deck).contains("<title>Demo lesson</title>", "h1 { color: teal; }", "// controls"),
                () -> assertThat(deck.indexOf("<h1>Hello</h1>")).isLessThan(deck.indexOf("<h1>The code under test"))
                        .isLessThan(deck.indexOf("<h1>Bye</h1>")),
                () -> assertThat(deck).contains("<li class=\"point\">The thing &amp; its size</li>",
                        "<figcaption>the thing</figcaption><pre>  1  class Thing {\n  2      int size()",
                        "<pre class=\"command\">$ ./gradlew test</pre>", "<p class=\"callout\">It passes.</p>",
                        "<aside class=\"notes\">Hi there.</aside>",
                        "<aside class=\"notes\">Here is the code &amp; its tests."),
                () -> assertThat(deck).doesNotContain("<li class=\"point\">The code under test</li>"),
                () -> assertThat(media.resolve("demo.deck.pdf")).exists(),
                () -> assertThat(Media.lessons(root, "demo")).hasSize(1));
    }
}
