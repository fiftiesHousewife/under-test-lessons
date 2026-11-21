package io.github.fiftieshousewife.undertest.techniques.structured;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * BEFORE: comparing the whole text broke on every run, because the id and the time change. So someone loosened it to
 * "contains": it now passes whatever the numbers are.
 */
class ContainsTextTest {

    @Test
    void summaryMentionsEveryItemAndATotal() {
        assertThat(Summaries.jsonForTheOrder()).contains("\"Tea\"", "\"Biscuits\"", "\"totalPence\"");
    }
}
