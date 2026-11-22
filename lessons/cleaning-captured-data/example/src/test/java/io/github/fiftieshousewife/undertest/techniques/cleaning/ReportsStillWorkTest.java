package io.github.fiftieshousewife.undertest.techniques.cleaning;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;

/** AFTER: the shop's reports give the same answers on the cleaned capture as on the capture before cleaning. */
class ReportsStillWorkTest {

    private final Capture captured = Captures.capturedDay();

    @Test
    void ordersAreStillGroupedByCustomer() throws GeneralSecurityException {
        final Capture cleaned = Captures.cleaning().clean(captured);
        assertThat(DayReport.ordersPerCustomer(cleaned)).as("orders per customer")
                .isEqualTo(DayReport.ordersPerCustomer(captured));
    }

    @Test
    void ordersStillMatchPaymentsByAccount() throws GeneralSecurityException {
        final Capture cleaned = Captures.cleaning().clean(captured);
        assertThat(DayReport.unsettledAccounts(cleaned)).as("pence owed by each unsettled account")
                .isEqualTo(DayReport.unsettledAccounts(captured));
    }
}
