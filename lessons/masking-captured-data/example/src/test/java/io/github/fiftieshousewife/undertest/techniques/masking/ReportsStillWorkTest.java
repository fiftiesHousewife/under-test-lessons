package io.github.fiftieshousewife.undertest.techniques.masking;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Test;

/** AFTER: the shop's reports give the same answers on the masked capture as on the capture before masking. */
class ReportsStillWorkTest {

    private final Capture captured = Captures.capturedDay();

    @Test
    void ordersAreStillGroupedByCustomer() throws GeneralSecurityException {
        final Capture masked = Captures.masking().mask(captured);
        assertThat(DayReport.ordersPerCustomer(masked)).as("orders per customer")
                .isEqualTo(DayReport.ordersPerCustomer(captured));
    }

    @Test
    void ordersStillMatchPaymentsByAccount() throws GeneralSecurityException {
        final Capture masked = Captures.masking().mask(captured);
        assertThat(DayReport.unsettledAccounts(masked)).as("pence owed by each unsettled account")
                .isEqualTo(DayReport.unsettledAccounts(captured));
    }
}
