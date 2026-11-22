package io.github.fiftieshousewife.undertest.techniques.batch.today;

import io.github.fiftieshousewife.undertest.techniques.batch.SettlementBatch;
import io.github.fiftieshousewife.undertest.techniques.batch.SettlementLine;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** TODAY: a header and one line per payment. The bank also needs a closing line with the count and total. */
@Configuration(proxyBeanMethods = false)
@Import(SettlementBatch.class)
public class TodaysSettlementJob {

    /** The writer: the header, then one line per payment, and nothing after them. */
    @Bean
    @StepScope
    public FlatFileItemWriter<SettlementLine> settlementFile(
            @Value("#{jobParameters['settlement']}") final String path) {
        return SettlementBatch.settlementFile(path).build();
    }
}
