package io.github.fiftieshousewife.undertest.techniques.batch.fixed;

import io.github.fiftieshousewife.undertest.techniques.batch.SettlementLine;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileFooterCallback;

/** FIXED: counts and adds up the lines as they are written, then writes the closing line: {@code TRAILER,3,121.49}. */
final class SettlementTotals implements ItemWriter<SettlementLine>, FlatFileFooterCallback {

    private int count;
    private BigDecimal total = BigDecimal.ZERO;

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
