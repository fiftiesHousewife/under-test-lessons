package io.github.fiftieshousewife.undertest.techniques.examples.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.examples.Policies;
import io.github.fiftieshousewife.undertest.techniques.examples.ReturnedItem;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvFileSource;

/*
 * THE EXERCISE'S ANSWER: the row the exercise adds to the returns team's examples, the 30th day without a receipt, in
 * a copy of their table with the same columns. It is the same boundary as their 30th-day example, on the other side of
 * the receipt rule: the team's rules give store credit, and today's code gives nothing. Run it against today's refunds
 * with {@code -Pcode=answer}.
 */
class ThirtiethDayAnswerTest {

    @ParameterizedTest(name = "{0}", quoteTextArguments = false)
    @CsvFileSource(resources = "/answer/returns-team-examples.csv", numLinesToSkip = 1)
    void returnsTeamExample(final ArgumentsAccessor row) {
        final boolean receipt = "yes".equals(row.getString(3));
        final ReturnedItem item = new ReturnedItem(row.get(1, LocalDate.class), row.get(2, LocalDate.class), receipt);
        assertThat(Policies.underTest().refundFor(item).words()).as(row.getString(0)).isEqualTo(row.getString(4));
    }
}
