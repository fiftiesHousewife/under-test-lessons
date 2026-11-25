package io.github.fiftieshousewife.undertest.techniques.flags.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.flags.Checkout;
import io.github.fiftieshousewife.undertest.techniques.flags.Checkouts;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: two baskets check only two prices. While both rules exist, the new ones must charge what the
 * old ones charge for every basket, here every penny from nothing to £100. Run it against today's new rules with
 * {@code -Pcode=answer}: it counts the baskets where they disagree, and names the first.
 */
class OldAndNewAgreeAnswerTest {

    private static final int UP_TO_PENCE = 10_000;

    @Test
    void theNewRulesChargeWhatTheOldOnesDoForEveryBasketUpToAHundredPounds() {
        final List<Integer> old = charges(Checkouts.withNewShipping(false));
        final List<Integer> replacement = charges(Checkouts.withNewShipping(true));

        final List<Integer> disagree = IntStream.rangeClosed(0, UP_TO_PENCE)
                .filter(basket -> !old.get(basket).equals(replacement.get(basket))).boxed().toList();

        final String first = disagree.isEmpty() ? "none" : disagree.get(0) + "p";
        assertThat(disagree.size()).as("baskets where the old and new rules disagree, the first: %s", first).isZero();
    }

    private static List<Integer> charges(final Checkout checkout) {
        return IntStream.rangeClosed(0, UP_TO_PENCE).map(checkout::shippingPence).boxed().toList();
    }
}
