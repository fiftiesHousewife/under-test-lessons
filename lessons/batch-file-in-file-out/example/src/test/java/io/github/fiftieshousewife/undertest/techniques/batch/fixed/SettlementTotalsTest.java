package io.github.fiftieshousewife.undertest.techniques.batch.fixed;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.batch.SettlementLine;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ExecutionContext;

/**
 * The closing line after a restart. Spring Batch saves each writer's state with every committed chunk and, when a
 * failed step is run again, starts a new writer from what was saved; these tests drive the totals the same way.
 */
class SettlementTotalsTest {

    private static final Chunk<SettlementLine> FIRST = Chunk.of(line("GB01", "10.00"), line("GB02", "20.50"));
    private static final Chunk<SettlementLine> AFTER_THE_RESTART = Chunk.of(line("GB03", "5.25"));

    @Test
    void aRestartedStepCountsTheLinesWrittenBeforeItFailed() throws IOException {
        final ExecutionContext saved = new ExecutionContext();
        final SettlementTotals firstRun = new SettlementTotals();
        firstRun.open(saved);
        firstRun.write(FIRST);
        firstRun.update(saved);

        final SettlementTotals restarted = new SettlementTotals();
        restarted.open(saved);
        restarted.write(AFTER_THE_RESTART);

        assertThat(closingLine(restarted)).isEqualTo("TRAILER,3,35.75");
    }

    @Test
    void aFreshStepStartsFromNothing() throws IOException {
        final SettlementTotals totals = new SettlementTotals();
        totals.open(new ExecutionContext());
        totals.write(AFTER_THE_RESTART);

        assertThat(closingLine(totals)).isEqualTo("TRAILER,1,5.25");
    }

    private static String closingLine(final SettlementTotals totals) throws IOException {
        final StringWriter closing = new StringWriter();
        totals.writeFooter(closing);
        return closing.toString();
    }

    private static SettlementLine line(final String account, final String amount) {
        return new SettlementLine(account, new BigDecimal(amount));
    }
}
