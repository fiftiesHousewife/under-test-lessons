package io.github.fiftieshousewife.undertest.techniques.batch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** The nightly job: reads the day's payments file, and writes the settlement file the bank collects. */
public interface SettlementJob {

    /** Turns the payments into the lines of the settlement file. */
    List<String> settle(List<Payment> payments);

    /** Runs the whole job, file in and file out. */
    default void run(final Path paymentsFile, final Path settlementFile) {
        try {
            final List<Payment> payments = Files.readAllLines(paymentsFile, StandardCharsets.UTF_8).stream()
                    .filter(line -> !line.isBlank()).map(Payment::parse).toList();
            Files.write(settlementFile, settle(payments), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
