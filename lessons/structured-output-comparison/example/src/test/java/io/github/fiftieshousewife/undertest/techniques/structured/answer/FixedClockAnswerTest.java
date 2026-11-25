package io.github.fiftieshousewife.undertest.techniques.structured.answer;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import io.github.fiftieshousewife.undertest.techniques.structured.Line;
import io.github.fiftieshousewife.undertest.techniques.structured.OrderSummary;
import io.github.fiftieshousewife.undertest.techniques.structured.fixed.FixedOrderSummary;
import io.github.fiftieshousewife.undertest.techniques.structured.today.TodaysOrderSummary;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: the time comes from a clock, so a test can give the summary a stopped clock and check the
 * time exactly, instead of accepting any text. Only the id, which is new on every run, stays a placeholder. Run it
 * against today's total with {@code -Pcode=answer}: it fails on the total alone, because the time now matches.
 */
class FixedClockAnswerTest {

    private static final Clock STOPPED = Clock.fixed(Instant.parse("2026-03-01T09:30:00Z"), ZoneOffset.UTC);

    @Test
    void summaryHasTheTimeFromItsClock() {
        final OrderSummary summary = Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysOrderSummary(STOPPED) : new FixedOrderSummary(STOPPED);
        assertThatJson(summary.json(List.of(new Line("Tea", 250, 4), new Line("Biscuits", 120, 1))))
                .isEqualTo("""
                        {
                          "lines": [
                            {"item": "Tea", "pricePence": 250, "quantity": 4},
                            {"item": "Biscuits", "pricePence": 120, "quantity": 1}
                          ],
                          "totalPence": 1120,
                          "id": "${json-unit.any-string}",
                          "generatedAt": "2026-03-01T09:30:00Z"
                        }
                        """);
    }
}
