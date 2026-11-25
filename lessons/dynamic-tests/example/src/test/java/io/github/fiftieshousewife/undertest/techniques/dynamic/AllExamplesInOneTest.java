package io.github.fiftieshousewife.undertest.techniques.dynamic;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/* BEFORE: one test loops over every example. It stops at the first wrong one, and its name never says which. */
class AllExamplesInOneTest {

    @Test
    void everyExampleFormatsAsExpected() {
        for (final Path example : Formatters.examples()) {
            final List<String> lines = Formatters.lines(example);
            assertThat(Formatters.current().forLetter(lines.get(0))).isEqualTo(lines.get(1));
        }
    }
}
