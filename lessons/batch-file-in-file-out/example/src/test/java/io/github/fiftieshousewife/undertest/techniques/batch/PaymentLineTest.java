package io.github.fiftieshousewife.undertest.techniques.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/** BEFORE: tests of the pieces: one line read, one line written. They pass, and never see the file the bank gets. */
class PaymentLineTest {

    @Test
    void readsAPaymentAndWritesItsSettlementLine() {
        final Payment payment = Payment.parse("12345678,1250");

        assertAll(
                () -> assertThat(payment).isEqualTo(new Payment("12345678", 1250)),
                () -> assertThat(payment.settlementLine()).isEqualTo("PAY,12345678,12.50"),
                () -> assertThat(new Payment("1", 899).settlementLine()).isEqualTo("PAY,1,8.99"));
    }
}
