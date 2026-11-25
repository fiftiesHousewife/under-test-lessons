package io.github.fiftieshousewife.undertest.techniques.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * AFTER: the whole job, file in and file out, run by Spring Batch's test library. Each test writes into a fresh
 * temporary folder that JUnit deletes afterwards, reads the settlement file back as records, and compares their fields.
 */
@SpringBatchTest
@SpringJUnitConfig(Jobs.class)
class WholeJobTest {

    @Autowired
    private JobOperatorTestUtils batch;

    @TempDir
    Path folder;

    @Test
    void syntheticPaymentsGiveTheirRecordsAndTheClosingTotals() throws Exception {
        final List<Payment> payments = SyntheticPayments.forTheDay(12);

        final Path settlement = runJob(SyntheticPayments.write(payments, folder.resolve("payments.csv")));

        assertSameRecords(SettlementFile.read(settlement), SettlementFile.expectedFor(payments));
    }

    @Test
    void aMaskedCaptureGivesTheSettlementFileTheBankAccepted() throws Exception {
        final Path settlement = runJob(Jobs.knownFile("captured-payments.csv"));

        assertSameRecords(SettlementFile.read(settlement),
                SettlementFile.read(Jobs.knownFile("captured-settlement.csv")));
    }

    private Path runJob(final Path payments) throws Exception {
        final Path settlement = folder.resolve("settlement.csv");
        final var parameters = new JobParametersBuilder().addString("payments", payments.toString())
                .addString("settlement", settlement.toString()).toJobParameters();
        assertThat(batch.startJob(parameters).getStatus()).as("job").isEqualTo(BatchStatus.COMPLETED);
        return settlement;
    }

    private static void assertSameRecords(final SettlementFile actual, final SettlementFile expected) {
        assertAll(
                () -> assertThat(actual.header()).as("header").isEqualTo(expected.header()),
                () -> assertThat(actual.payments()).as("payment records")
                        .containsExactlyElementsOf(expected.payments()),
                () -> assertThat(actual.closing()).as("closing line").isEqualTo(expected.closing()));
    }
}
