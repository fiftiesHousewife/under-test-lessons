package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RecordingScriptTest {

    private static final String HELLO = "Here is PIT. [pause 1600ms] It reads the version catalogue.";
    private static final String BYE = "Goodbye.";

    @TempDir
    Path root;

    private Path media;

    @BeforeEach
    void lesson() throws IOException {
        media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\nSay: " + HELLO + "\n"
                + "## closing: Bye\n- A point\nSay: " + BYE + "\n");
        Files.writeString(Files.createDirectories(root.resolve("lessons/_shared")).resolve("pronunciations.txt"),
                "PIT = pit\nversion catalogue = /vˈɜːʃən katəlɒɡ/\nrewrite = /ɹˈiːɹaɪt/\n");
    }

    private String script(final boolean missingOnly) throws IOException {
        final Path page = root.resolve("build/script.html");
        RecordingScript.write(new Media(root, "demo"), missingOnly, page);
        return Files.readString(page);
    }

    @Test
    void listsEachClipWithItsFileTitleWordsPausesAndHints() throws IOException {
        final String page = script(false);

        assertAll(
                () -> assertThat(page).contains("<b>1. Hello</b> · save as <span class=\"file\">"
                        + new Spoken(HELLO, HELLO).clipStem() + ".m4a</span>"),
                () -> assertThat(page).contains("Here is PIT. <span class=\"pause\">(pause 1.6 s)</span> It reads"),
                () -> assertThat(page).contains("PIT: say <i>pit</i> · version catalogue: stress <i>version</i>"),
                () -> assertThat(page).contains("<b>2. Bye</b>"));
    }

    @Test
    void withMissingListsOnlyTheSlidesThatHaveNoClip() throws IOException {
        final Path clips = Files.createDirectories(media.resolve("demo.deck-voice"));
        Files.writeString(clips.resolve(new Spoken(HELLO, HELLO).clipStem() + ".wav"), "recorded");

        final String page = script(true);

        assertAll(
                () -> assertThat(page).doesNotContain("1. Hello").contains("<b>2. Bye</b>"),
                () -> assertThat(script(false)).contains("1. Hello"));
    }

    @Test
    void aWordGivenAsSoundsIsHintedWithItsSounds() {
        final Map<String, String> used = new LinkedHashMap<>();
        used.put("rewrite", "/ɹˈiːɹaɪt/");
        used.put("the record", "the /ɹˈɛkɔːd/");

        assertThat(RecordingScript.hints(used)).containsExactly("rewrite: say it as <i>/ɹˈiːɹaɪt/</i>",
                "the record: say it as <i>the /ɹˈɛkɔːd/</i>");
    }
}
