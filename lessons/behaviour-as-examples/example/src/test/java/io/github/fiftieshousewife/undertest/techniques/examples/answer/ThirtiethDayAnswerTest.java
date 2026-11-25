package io.github.fiftieshousewife.undertest.techniques.examples.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.examples.Policies;
import io.github.fiftieshousewife.undertest.techniques.examples.ReturnedItem;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/*
 * THE EXERCISE'S ANSWER: one more row the returns team would write, the 30th day without a receipt. It is the same
 * boundary as their 30th-day example, on the other side of the receipt rule: the team's rules give store credit, and
 * today's code gives nothing. Run it against today's refunds with {@code -Pcode=answer}.
 */
class ThirtiethDayAnswerTest {

    private static final LocalDate BOUGHT = LocalDate.of(2027, 3, 1);

    @ParameterizedTest(name = "{0}", quoteTextArguments = false)
    @CsvSource(textBlock = """
            returned on the 30th day without a receipt, 30, store credit
            """)
    void returnsTeamExample(final String example, final int daysAfterBuying, final String refund) {
        final ReturnedItem item = new ReturnedItem(BOUGHT, BOUGHT.plusDays(daysAfterBuying), false);
        assertThat(Policies.underTest().refundFor(item).words()).as(example).isEqualTo(refund);
    }
}
