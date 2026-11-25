package io.github.fiftieshousewife.undertest.techniques.jakartaee;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/* BEFORE: a unit test that builds the checkout by hand and hands it the standard rate itself. */
class CheckoutByHandTest {

    @Test
    void addsVatAtTheStandardRate() {
        assertThat(Checkouts.byHand(new StandardRate()).totalPence(1000)).isEqualTo(1200);
    }
}
