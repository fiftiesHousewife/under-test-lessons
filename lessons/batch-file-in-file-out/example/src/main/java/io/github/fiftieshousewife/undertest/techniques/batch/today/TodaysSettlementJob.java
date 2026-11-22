package io.github.fiftieshousewife.undertest.techniques.batch.today;

import io.github.fiftieshousewife.undertest.techniques.batch.Payment;
import io.github.fiftieshousewife.undertest.techniques.batch.SettlementJob;
import java.util.ArrayList;
import java.util.List;

/** TODAY: a header and one line per payment. The bank also needs a closing line with the count and total. */
public final class TodaysSettlementJob implements SettlementJob {

    @Override
    public List<String> settle(final List<Payment> payments) {
        final List<String> lines = new ArrayList<>(List.of("HEADER,SETTLEMENT"));
        payments.forEach(payment -> lines.add(payment.settlementLine()));
        return lines;
    }
}
