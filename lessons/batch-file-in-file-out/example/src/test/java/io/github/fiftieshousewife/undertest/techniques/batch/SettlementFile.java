package io.github.fiftieshousewife.undertest.techniques.batch;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/*
 * A settlement file read back as records, the way the bank reads it: the header must be the first line and the
 * closing line the last, so a header or closing line missing or out of place reads as {@code null}.
 */
record SettlementFile(String header, List<PaymentRecord> payments, ClosingRecord closing) {

    /* One payment's line: {@code PAY,12345678,12.50}. */
    record PaymentRecord(String account, BigDecimal amount) {
    }

    /* The closing line: {@code TRAILER,3,121.49}, the number of payments and their total. */
    record ClosingRecord(int count, BigDecimal total) {
    }

    static SettlementFile read(final Path file) throws IOException {
        final List<String[]> rows = Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                .map(line -> line.split(",", -1)).toList();
        final String[] first = rows.isEmpty() ? new String[] {""} : rows.getFirst();
        final String[] last = rows.isEmpty() ? new String[] {""} : rows.getLast();
        return new SettlementFile(
                "HEADER".equals(first[0]) ? first[1] : null,
                rows.stream().filter(row -> "PAY".equals(row[0]))
                        .map(row -> new PaymentRecord(row[1], new BigDecimal(row[2]))).toList(),
                "TRAILER".equals(last[0])
                        ? new ClosingRecord(Integer.parseInt(last[1]), new BigDecimal(last[2])) : null);
    }

    /* What the bank expects for these payments: each one in pounds, in order, then their count and total. */
    static SettlementFile expectedFor(final List<Payment> payments) {
        final List<PaymentRecord> records = payments.stream()
                .map(payment -> new PaymentRecord(payment.account(), BigDecimal.valueOf(payment.pence(), 2))).toList();
        final BigDecimal total = records.stream().map(PaymentRecord::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SettlementFile("SETTLEMENT", records, new ClosingRecord(records.size(), total));
    }
}
