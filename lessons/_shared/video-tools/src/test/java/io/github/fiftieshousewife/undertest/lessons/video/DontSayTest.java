package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* A phrase on the don't-say list fails the lesson that says it, with the reason and where it was said. */
class DontSayTest {

    @TempDir
    Path root;

    @Test
    void readsEachRuleAndItsReason() {
        final List<DontSay.Rule> rules = DontSay.rules(List.of("# a comment", "", "\\bnobody\\b = an invented actor"));

        assertAll(
                () -> assertThat(rules).hasSize(1),
                () -> assertThat(rules.getFirst().phrase().pattern()).isEqualTo("\\bnobody\\b"),
                () -> assertThat(rules.getFirst().why()).isEqualTo("an invented actor"));
    }

    @Test
    void namesThePhraseTheNarrationSaysAndWhy() throws IOException {
        Files.createDirectories(root.resolve("lessons/_shared"));
        Files.write(root.resolve(DontSay.LIST), List.of("\\bnobody\\b = an invented actor"));
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.write(media.resolve("demo.narration.md"), List.of("# Narration", "", "1. A customer nobody has.",
                "2. The shop refunds the order."));

        assertThat(DontSay.problems(new Media(root, "demo"))).singleElement().extracting(Problem::message).asString()
                .contains("/\\bnobody\\b/", "an invented actor", "A customer nobody has.");
    }
}
