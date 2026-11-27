package io.github.fiftieshousewife.undertest.techniques.jakartaee;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/*
 * AFTER: Weld, the CDI container application servers use, starts inside the test with the application's beans. The
 * container wires the checkout exactly as it would in production, without a server.
 */
@ExtendWith(WeldJunit5Extension.class)
class CheckoutInContainerTest {

    @WeldSetup
    WeldInitiator weld = WeldInitiator.of(Checkouts.chosen(), StandardRate.class, ReducedRate.class);

    @Test
    void addsVatAtTheStandardRate() {
        assertThat(weld.select(Checkouts.chosen()).get().totalCents(1000)).as("€10 with VAT").isEqualTo(1200);
    }

    @Test
    void theReducedRateIsStillThereForThingsThatNeedIt() {
        assertThat(weld.select(ReducedRate.class).get().percent()).isEqualTo(5);
    }
}
