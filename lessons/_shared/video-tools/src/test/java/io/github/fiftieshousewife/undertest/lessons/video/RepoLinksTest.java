package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class RepoLinksTest {

    @AfterEach
    void restore() {
        System.clearProperty("video.lessonsRepo");
        System.clearProperty("video.toolkitRepo");
    }

    @Test
    void fillsInConfiguredAddressesAndNamesTheRestInWords() {
        System.setProperty("video.lessonsRepo", "https://git.example.com/testing/lessons");

        assertAll(
                () -> assertThat(RepoLinks.fill("{lessons-repo} — this lesson"))
                        .isEqualTo("https://git.example.com/testing/lessons — this lesson"),
                () -> assertThat(RepoLinks.fill("{toolkit-repo} — the toolkit these lessons belong to"))
                        .isEqualTo("The toolkit these lessons belong to"),
                () -> assertThat(RepoLinks.fill("{lessons-repo}/tree/main/lessons/tags — this lesson"))
                        .isEqualTo("https://git.example.com/testing/lessons/tree/main/lessons/tags — this lesson"),
                () -> assertThat(RepoLinks.fill("{toolkit-repo}/tree/main/docs — the toolkit's guides"))
                        .isEqualTo("The toolkit's guides"),
                () -> assertThat(RepoLinks.fill("No placeholder")).isEqualTo("No placeholder"));
    }
}
