package io.github.fiftieshousewife.undertest.techniques.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/*
 * Pins down the bug the property test found in the splitter in use today, as an example test, so it stays written
 * down: with more than one penny left over, the first person pays all of it.
 */
class FirstPayerSplitterTest {

    @Test
    void givesTheFirstPersonEveryPennyLeftOver() {
        assertAll(
                () -> assertThat(new FirstPayerSplitter().split(3, 4)).containsExactly(3, 0, 0, 0),
                () -> assertThat(new FairSplitter().split(3, 4)).containsExactly(1, 1, 1, 0));
    }
}
