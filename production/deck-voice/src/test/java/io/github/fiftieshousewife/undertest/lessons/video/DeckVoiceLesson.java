package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

/* A demo lesson with a slide deck, and stand-ins for the voice and ffmpeg, for the tests of the deck's voice. */
class DeckVoiceLesson {

    @TempDir
    Path root;

    Path media;
    Path voice;

    @BeforeEach
    void lesson() throws IOException {
        media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.writeString(media.resolve("demo.scenes"), "## Scene 1: one\n");
        Files.writeString(media.resolve("demo.narration.md"), "1. Run PIT now.\n");
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\nSay: Hello there.\n"
                + "## closing: Bye\n- A point\nSay: Goodbye.\n");
        final Path shared = Files.createDirectories(root.resolve("lessons/_shared/slides/pictures"));
        Files.writeString(shared.resolveSibling("slides.css"), "body { }");
        Files.writeString(shared.resolveSibling("deck.css"), ".deck { }");
        Files.writeString(shared.resolveSibling("deck.js"), "// controls");
        Files.writeString(root.resolve("lessons/_shared/pronunciations.txt"), "PIT = pit\n");
        new WavFile(100, 1, new byte[200]).write(root.resolve("clip.wav"));
        voice = standIn("voice", "echo \"$1\" >> " + root.resolve("spoken.log") + "; cp " + root.resolve("clip.wav")
                + " \"$2\"");
        System.setProperty("video.ffmpeg", standIn("ffmpeg", "for last; do :; done; touch \"$last\"").toString());
    }

    @AfterEach
    void restore() {
        System.clearProperty("video.ffmpeg");
        System.clearProperty(Speech.READS_LISTS);
        System.clearProperty(Speech.ENGINE_PROPERTY);
    }

    Path standIn(final String name, final String body) throws IOException {
        return standIn(root, name, body);
    }

    /* A shell script in {@code root} that stands in for a program, made executable. */
    static Path standIn(final Path root, final String name, final String body) throws IOException {
        final Path script = root.resolve(name);
        Files.writeString(script, "#!/bin/sh\n" + body + "\n");
        assertThat(script.toFile().setExecutable(true)).isTrue();
        return script;
    }

    static String clip(final String written) {
        return new Spoken(written, written).clipName();
    }

    List<String> deckSays() {
        return DeckVoice.spoken(new Media(root, "demo")).stream().map(Spoken::said).toList();
    }

    long spokenParts() throws IOException {
        final Path log = root.resolve("spoken.log");
        return Files.isRegularFile(log) ? Files.readAllLines(log).size() : 0;
    }
}
