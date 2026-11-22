package io.github.fiftieshousewife.undertest.techniques.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * AFTER: the rules as the returns team wrote them down, one named example per row, in a table they can open and edit
 * in a spreadsheet. Each row runs, and is reported, by the team's own name for it. A row that fails is a question for
 * the team, not a test to delete.
 */
class ReturnsTeamExamplesTest {

    private final RefundPolicy policy = Policies.underTest();

    @ParameterizedTest(name = "{0}", quoteTextArguments = false)
    @CsvFileSource(resources = "/returns-team-examples.csv", numLinesToSkip = 1)
    void returnsTeamExample(final ArgumentsAccessor row) {
        final String example = row.getString(0);
        final ReturnedItem item = new ReturnedItem(row.get(1, LocalDate.class), row.get(2, LocalDate.class),
                "yes".equals(row.getString(3)));

        assertThat(policy.refundFor(item).words()).as(example).isEqualTo(row.getString(4));
    }
}
