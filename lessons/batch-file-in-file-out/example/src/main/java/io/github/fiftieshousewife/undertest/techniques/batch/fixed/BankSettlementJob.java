package io.github.fiftieshousewife.undertest.techniques.batch.fixed;

import io.github.fiftieshousewife.undertest.techniques.batch.SettlementBatch;
import io.github.fiftieshousewife.undertest.techniques.batch.SettlementLine;
import java.util.List;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** FIXED: a header, one line per payment, and the closing line with the count and total the bank checks. */
@Configuration(proxyBeanMethods = false)
@Import(SettlementBatch.class)
public class BankSettlementJob {

    /** The writer: the same lines, counted and added up as they are written, then the closing line. */
    @Bean
    @StepScope
    public CompositeItemWriter<SettlementLine> settlementFile(
            @Value("#{jobParameters['settlement']}") final String path) {
        final SettlementTotals totals = new SettlementTotals();
        final ItemWriter<SettlementLine> file = SettlementBatch.settlementFile(path).footerCallback(totals).build();
        return new CompositeItemWriter<>(List.of(file, totals));
    }
}
