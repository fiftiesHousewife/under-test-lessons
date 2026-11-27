package io.github.fiftieshousewife.undertest.techniques.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/*
 * BEFORE: example tests. Each checks one case the author thought of. The cases all leave a remainder of 0 or 1 cent,
 * so the splitter in use today passes them all, even though it is unfair whenever more is left over.
 */
class SplitterExampleTest {

    private final BillSplitter splitter = Splitters.current();

    @Test
    void splitsEvenly() {
        assertThat(splitter.split(900, 3)).containsExactly(300, 300, 300);
    }

    @Test
    void givesTheOddCentToSomeone() {
        assertAll(
                () -> assertThat(splitter.split(100, 3)).containsExactly(34, 33, 33),
                () -> assertThat(splitter.split(101, 2)).containsExactly(51, 50));
    }
}
