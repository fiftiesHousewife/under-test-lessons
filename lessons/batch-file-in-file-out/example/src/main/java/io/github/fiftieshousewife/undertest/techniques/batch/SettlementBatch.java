package io.github.fiftieshousewife.undertest.techniques.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

/**
 * The nightly Spring Batch job, apart from its writer: it reads the payments file named by the job's "payments"
 * parameter, turns each payment into its settlement line, and writes the file named by "settlement". Today's job and
 * the fixed one each add the writer. Every settlement file starts with the line {@code HEADER}.
 */
@Configuration(proxyBeanMethods = false)
@EnableBatchProcessing
public class SettlementBatch {

    public static final String HEADER = "HEADER,SETTLEMENT";

    private static final int PAYMENTS_PER_CHUNK = 100;

    /** The reader: one payment from each line of the day's payments file. */
    @Bean
    @StepScope
    public FlatFileItemReader<Payment> payments(@Value("#{jobParameters['payments']}") final String path) {
        return new FlatFileItemReaderBuilder<Payment>().name("payments").resource(new FileSystemResource(path))
                .lineMapper((line, lineNumber) -> Payment.parse(line)).build();
    }

    /** The job: one step, which settles the day's payments. */
    @Bean
    public Job settlementJob(final JobRepository jobRepository, final Step settle) {
        return new JobBuilder("settlement", jobRepository).start(settle).build();
    }

    /** The step: read each payment, process it into its settlement line, and write it with each version's writer. */
    @Bean
    public Step settle(final JobRepository jobRepository, final ItemReader<Payment> payments,
            final ItemWriter<SettlementLine> settlementFile) {
        return new StepBuilder("settle", jobRepository).<Payment, SettlementLine>chunk(PAYMENTS_PER_CHUNK)
                .reader(payments).processor(SettlementLine::of).writer(settlementFile).build();
    }

    /** The writer both versions start from: the header, then one line per payment, each ending in a line feed. */
    public static FlatFileItemWriterBuilder<SettlementLine> settlementFile(final String path) {
        return new FlatFileItemWriterBuilder<SettlementLine>().name("settlement").resource(new FileSystemResource(path))
                .lineSeparator("\n").headerCallback(writer -> writer.write(HEADER))
                .lineAggregator(SettlementLine::text);
    }
}
