package io.github.fiftieshousewife.undertest.techniques.batch;

import io.github.fiftieshousewife.undertest.techniques.batch.fixed.BankSettlementJob;
import io.github.fiftieshousewife.undertest.techniques.batch.today.TodaysSettlementJob;
import java.net.URISyntaxException;
import java.nio.file.Path;

/** The job the tests run (the fixed one, or today's with -Pcode=today), and the known files it is tested with. */
final class Jobs {

    private Jobs() {
    }

    static SettlementJob current() {
        return "today".equals(System.getProperty("code")) ? new TodaysSettlementJob() : new BankSettlementJob();
    }

    static Path knownFile(final String name) {
        try {
            return Path.of(Jobs.class.getResource("/settlement/" + name).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
