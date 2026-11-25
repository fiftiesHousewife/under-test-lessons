package io.github.fiftieshousewife.undertest.techniques.batch.today;

import io.github.fiftieshousewife.undertest.techniques.batch.SettlementBatch;
import io.github.fiftieshousewife.undertest.techniques.batch.SettlementLine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** TODAY: a header and one line per payment. The bank also needs a closing line with the count and total. */
@Configuration(proxyBeanMethods = false)
@Import(SettlementBatch.class)
public class TodaysSettlementJob {

    /** The writer: the header, then one line per payment, and nothing after them, written anew for every chunk. */
    @Bean
    @StepScope
    public ItemWriter<SettlementLine> settlementFile(@Value("#{jobParameters['settlement']}") final String path) {
        return chunk -> Files.writeString(Path.of(path), Stream.concat(Stream.of(SettlementBatch.HEADER),
                chunk.getItems().stream().map(SettlementLine::text)).collect(Collectors.joining("\n", "", "\n")));
    }
}
