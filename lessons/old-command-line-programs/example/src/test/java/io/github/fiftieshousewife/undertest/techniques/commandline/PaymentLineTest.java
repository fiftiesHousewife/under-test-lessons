package io.github.fiftieshousewife.undertest.techniques.commandline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/**
 * BEFORE: tests of the pieces. They read one line and check the limit, but never run the program: not its
 * environment variable, its input, what it prints, or how it ends.
 */
class PaymentLineTest {

    @Test
    void readsTheAccountAndTheAmount() {
        assertThat(Payment.parse("ACC-1001,2500")).isEqualTo(new Payment("ACC-1001", 2500));
    }

    @Test
    void allowsAPaymentOfExactlyTheLimit() {
        assertAll(
            () -> assertThat(new Payment("ACC-1001", 50_000).isOver(50_000)).isFalse(),
            () -> assertThat(new Payment("ACC-1001", 50_001).isOver(50_000)).isTrue());
    }

    @Test
    void refusesALineWithoutAnAmount() {
        assertThatThrownBy(() -> Payment.parse("ACC-1001")).hasMessageContaining("an account and an amount");
    }
}
