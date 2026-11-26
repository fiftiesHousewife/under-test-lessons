package io.github.fiftieshousewife.undertest.techniques.masking.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.masking.Capture;
import io.github.fiftieshousewife.undertest.techniques.masking.DayReport;
import io.github.fiftieshousewife.undertest.techniques.masking.testdata.TestData;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: a second captured day, masked the same way, where one customer paid an order twice and
 * another paid only part of one. The report must still give the day's real answer. Run it against today's test data
 * with {@code -Pcode=answer}: fixed text merges both accounts into one, and the answer goes wrong.
 */
class SecondDayAnswerTest {

    @Test
    void listsWhatEachUnsettledAccountOwesOnTheSecondDay() {
        final Capture day = TestData.masked("capture-day-2");

        assertThat(DayReport.unsettledAccounts(day)).as("pence owed by each unsettled account")
                .containsExactly(-700, 200);
    }
}
