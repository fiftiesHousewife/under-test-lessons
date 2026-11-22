package io.github.fiftieshousewife.undertest.techniques.batch.fixed;

import io.github.fiftieshousewife.undertest.techniques.batch.Payment;
import io.github.fiftieshousewife.undertest.techniques.batch.SettlementJob;
import java.util.ArrayList;
import java.util.List;

/** FIXED: a header, one line per payment, and the closing line with the count and total the bank checks. */
public final class BankSettlementJob implements SettlementJob {

    @Override
    public List<String> settle(final List<Payment> payments) {
        final List<String> lines = new ArrayList<>(List.of("HEADER,SETTLEMENT"));
        payments.forEach(payment -> lines.add(payment.settlementLine()));
        final int total = payments.stream().mapToInt(Payment::pence).sum();
        lines.add("TRAILER,%d,%d.%02d".formatted(payments.size(), total / 100, total % 100));
        return lines;
    }
}
