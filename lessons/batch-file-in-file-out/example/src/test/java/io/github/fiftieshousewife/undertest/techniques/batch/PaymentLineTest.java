package io.github.fiftieshousewife.undertest.techniques.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/* BEFORE: tests of the pieces: one line read, one line processed and written. They pass, and never see the file. */
class PaymentLineTest {

    @Test
    void readsAPaymentAndWritesItsSettlementLine() {
        final Payment payment = Payment.parse("12345678,1250");

        assertAll(
                () -> assertThat(payment).isEqualTo(new Payment("12345678", 1250)),
                () -> assertThat(SettlementLine.of(payment).text()).isEqualTo("PAY,12345678,12.50"),
                () -> assertThat(SettlementLine.of(new Payment("1", 899)).text()).isEqualTo("PAY,1,8.99"));
    }
}
