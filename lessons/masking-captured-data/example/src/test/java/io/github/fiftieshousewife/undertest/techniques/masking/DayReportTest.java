package io.github.fiftieshousewife.undertest.techniques.masking;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.masking.testdata.TestData;
import org.junit.jupiter.api.Test;

/*
 * The tests of the code under test, the shop's reports, run on a captured day of real data, masked first. The answers
 * are the day's real ones: Ada Brennan placed three orders, Omar Haddad two, Grace Okafor and Lin Chen one each; Ada's
 * account still owes 640 pence, and Lin's paid 500 too many.
 */
class DayReportTest {

    private final Capture day = TestData.masked("capture");

    @Test
    void countsEachCustomersOrders() {
        assertThat(DayReport.ordersPerCustomer(day)).as("orders per customer").containsExactly(3, 2, 1, 1);
    }

    @Test
    void listsWhatEachUnsettledAccountOwes() {
        assertThat(DayReport.unsettledAccounts(day)).as("pence owed by each unsettled account")
                .containsExactly(-500, 640);
    }
}
