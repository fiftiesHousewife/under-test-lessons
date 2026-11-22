package io.github.fiftieshousewife.undertest.techniques.examples;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * BEFORE: tests a developer wrote by reading the code. They check what the code does, in the code's own terms, on days
 * far from any boundary, so they agree with it whatever it does.
 */
class RefundPolicyTest {

    private static final LocalDate BOUGHT = LocalDate.of(2027, 3, 1);

    private final RefundPolicy policy = Policies.underTest();

    @Test
    void refundsDependOnDaysAndReceipt() {
        assertAll(
                () -> assertThat(policy.refundFor(new ReturnedItem(BOUGHT, BOUGHT.plusDays(5), true)))
                        .isEqualTo(Refund.TO_CARD),
                () -> assertThat(policy.refundFor(new ReturnedItem(BOUGHT, BOUGHT.plusDays(60), true)))
                        .isEqualTo(Refund.STORE_CREDIT),
                () -> assertThat(policy.refundFor(new ReturnedItem(BOUGHT, BOUGHT.plusDays(5), false)))
                        .isEqualTo(Refund.STORE_CREDIT),
                () -> assertThat(policy.refundFor(new ReturnedItem(BOUGHT, BOUGHT.plusDays(60), false)))
                        .isEqualTo(Refund.NOTHING));
    }
}
