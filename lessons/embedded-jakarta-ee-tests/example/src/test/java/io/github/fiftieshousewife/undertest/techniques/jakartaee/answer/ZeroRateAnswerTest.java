package io.github.fiftieshousewife.undertest.techniques.jakartaee.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.jakartaee.Checkouts;
import io.github.fiftieshousewife.undertest.techniques.jakartaee.ReducedRate;
import io.github.fiftieshousewife.undertest.techniques.jakartaee.StandardRate;
import io.github.fiftieshousewife.undertest.techniques.jakartaee.TaxRate;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * THE EXERCISE'S ANSWER: one more rate arrives, a zero rate with no mark, as the reduced rate did. The container must
 * still give the checkout the standard rate. Run it against today's checkout with {@code -Pcode=answer}.
 */
@ExtendWith(WeldJunit5Extension.class)
class ZeroRateAnswerTest {

    /** The zero rate, 0%, for things like books. Added later still, and, like the reduced rate, with no qualifier. */
    @ApplicationScoped
    static class ZeroRate implements TaxRate {

        @Override
        public int percent() {
            return 0;
        }
    }

    @WeldSetup
    WeldInitiator weld = WeldInitiator.of(Checkouts.chosen(), StandardRate.class, ReducedRate.class, ZeroRate.class);

    @Test
    void aThirdRateLeavesTheCheckoutOnTheStandardRate() {
        assertThat(weld.select(Checkouts.chosen()).get().totalPence(1000)).isEqualTo(1200);
    }
}
