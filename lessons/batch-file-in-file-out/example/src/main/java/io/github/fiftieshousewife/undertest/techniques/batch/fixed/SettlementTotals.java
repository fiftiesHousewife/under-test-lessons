package io.github.fiftieshousewife.undertest.techniques.batch.fixed;

import io.github.fiftieshousewife.undertest.techniques.batch.SettlementLine;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStream;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileFooterCallback;

/**
 * FIXED: counts and adds up the lines as they are written, then writes the closing line: {@code TRAILER,3,121.49}.
 * The count and total are saved with each committed chunk and given back when a failed step restarts, so the closing
 * line covers every line in the file, not only those written since the restart.
 */
final class SettlementTotals implements ItemWriter<SettlementLine>, ItemStream, FlatFileFooterCallback {

    private static final String COUNT = "settlement.totals.count";
    private static final String TOTAL = "settlement.totals.total";

    private int count;
    private BigDecimal total = BigDecimal.ZERO;

    @Override
    public void open(final ExecutionContext saved) {
        count = saved.containsKey(COUNT) ? saved.getInt(COUNT) : 0;
        total = saved.containsKey(TOTAL) ? new BigDecimal(saved.getString(TOTAL)) : BigDecimal.ZERO;
    }

    @Override
    public void update(final ExecutionContext saved) {
        saved.putInt(COUNT, count);
        saved.putString(TOTAL, total.toPlainString());
    }

    @Override
    public void write(final Chunk<? extends SettlementLine> lines) {
        for (final SettlementLine line : lines) {
            count++;
            total = total.add(line.amount());
        }
    }

    @Override
    public void writeFooter(final Writer writer) throws IOException {
        writer.write("TRAILER," + count + "," + total.toPlainString());
    }
}
