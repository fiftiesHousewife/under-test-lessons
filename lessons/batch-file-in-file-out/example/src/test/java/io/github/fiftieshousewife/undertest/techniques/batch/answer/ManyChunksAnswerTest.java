package io.github.fiftieshousewife.undertest.techniques.batch.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.fiftieshousewife.undertest.techniques.batch.Jobs;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * THE EXERCISE'S ANSWER: the job writes in chunks of 100 payments, and the whole-job tests run only 6 and 12, one chunk
 * each. A day of 250 payments takes three chunks, so the closing line must count and add up across all of them:
 * payment n is n times 7 pence, 219,625 pence in all. Run it against today's job with {@code -Pcode=answer}.
 */
@SpringBatchTest
@SpringJUnitConfig(Jobs.class)
class ManyChunksAnswerTest {

    private static final int PAYMENTS = 250;

    @Autowired
    private JobOperatorTestUtils batch;

    @TempDir
    Path folder;

    @Test
    void theClosingLineCoversEveryChunk() throws Exception {
        final Path input = Files.write(folder.resolve("payments.csv"), IntStream.rangeClosed(1, PAYMENTS)
                .mapToObj(n -> String.format(Locale.ROOT, "%08d,%d", n, n * 7)).toList(), StandardCharsets.UTF_8);
        final Path settlement = folder.resolve("settlement.csv");

        final BatchStatus status = batch.startJob(new JobParametersBuilder().addString("payments", input.toString())
                .addString("settlement", settlement.toString()).toJobParameters()).getStatus();

        final List<String> lines = Files.readAllLines(settlement, StandardCharsets.UTF_8);
        assertAll(
                () -> assertThat(status).as("job").isEqualTo(BatchStatus.COMPLETED),
                () -> assertThat(lines.getLast()).as("closing line").isEqualTo("TRAILER,250,2196.25"));
    }
}
