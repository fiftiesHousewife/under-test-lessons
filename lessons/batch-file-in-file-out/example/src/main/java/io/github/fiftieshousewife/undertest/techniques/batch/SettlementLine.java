package io.github.fiftieshousewife.undertest.techniques.batch;

import java.math.BigDecimal;

/** One payment's line in the settlement file: the account, and the amount in pounds. */
public record SettlementLine(String account, BigDecimal amount) {

    /** The job's processing step: a payment in pence becomes its settlement line in pounds. */
    public static SettlementLine of(final Payment payment) {
        return new SettlementLine(payment.account(), BigDecimal.valueOf(payment.pence(), 2));
    }

    /** The line as the bank reads it: {@code PAY,12345678,12.50}. */
    public String text() {
        return "PAY," + account + "," + amount.toPlainString();
    }
}
