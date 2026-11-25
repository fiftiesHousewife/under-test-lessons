package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/*
 * The deck's Listen control keeps playing slide after slide in a browser that guards autoplay (as Safari does), and
 * says when the last slide has been read. Runs deck-listen.test.js with Node, where Node is installed.
 */
class DeckListenTest {

    @Test
    void keepsPlayingEverySlideAfterTheFirstClick() throws URISyntaxException {
        Assumptions.assumeTrue(Arrays.stream(System.getenv().getOrDefault("PATH", "").split(File.pathSeparator))
                .anyMatch(folder -> Files.isExecutable(Path.of(folder, "node"))), "needs Node");
        final Path test = Path.of(DeckListenTest.class.getResource("/deck-listen.test.js").toURI());
        final Path deck = Path.of("../slides/deck.js").toAbsolutePath().normalize();

        final String report = ExternalCommand.run(List.of("node", test.toString(), deck.toString()), test.getParent());

        assertThat(report).doesNotContain("FAIL").contains("ok   the third slide plays after the second");
    }
}
