package io.github.fiftieshousewife.undertest.techniques.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * AFTER: the whole job, file in and file out. It runs on a known payments file, writes into a fresh temporary folder
 * that JUnit deletes afterwards, and compares the settlement file with the expected one, line by line.
 */
class WholeJobTest {

    @TempDir
    Path folder;

    @Test
    void knownPaymentsGiveTheExpectedSettlementFile() throws IOException {
        final Path settlement = folder.resolve("settlement.csv");

        Jobs.current().run(Jobs.knownFile("payments.csv"), settlement);

        assertThat(Files.readAllLines(settlement)).as("settlement file")
                .containsExactlyElementsOf(Files.readAllLines(Jobs.knownFile("expected-settlement.csv")));
    }
}
