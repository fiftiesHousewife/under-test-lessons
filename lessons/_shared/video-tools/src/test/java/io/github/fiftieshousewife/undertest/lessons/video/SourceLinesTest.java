package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

class SourceLinesTest {

    @Test
    void picksLinesAsTheShowHelperDoes() {
        final List<String> toml = List.of("[versions]", "junit = \"6\"", "pit = \"1\"", "", "[libraries]", "x = 1",
                "pit-core = { module = \"p\" }");
        final List<String> kotlin = List.of("plugins {", "    id(\"java\")", "}", "", "pitest {", "    threads = 2",
                "    verbose = true", "}");

        assertAll(
                () -> assertThat(SourceLines.range(kotlin, 1, 2)).containsExactly("  1  plugins {",
                        "  2      id(\"java\")"),
                () -> assertThat(SourceLines.tomlMatches(toml, "pit")).containsExactly("  1  [versions]",
                        SourceLines.GAP, "  3  pit = \"1\"", SourceLines.GAP, "  5  [libraries]", SourceLines.GAP,
                        "  7  pit-core = { module = \"p\" }"),
                () -> assertThat(SourceLines.blockAround(kotlin, "^pitest")).hasSize(4).first().asString()
                        .contains("pitest {"),
                () -> assertThat(SourceLines.blockAround(kotlin, "verbose")).first().asString().contains("pitest {"),
                () -> assertThat(SourceLines.blockAround(kotlin, "nowhere")).isEmpty(),
                () -> assertThat(SceneContent.words("show \"a b\" 1 2")).containsExactly("show", "a b", "1", "2"));
    }
}
