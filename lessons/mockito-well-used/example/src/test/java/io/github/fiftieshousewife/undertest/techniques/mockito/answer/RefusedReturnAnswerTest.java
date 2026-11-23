package io.github.fiftieshousewife.undertest.techniques.mockito.answer;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import io.github.fiftieshousewife.undertest.techniques.mockito.CardPayments;
import io.github.fiftieshousewife.undertest.techniques.mockito.Refunds;
import io.github.fiftieshousewife.undertest.techniques.mockito.Shop;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** The exercise's answer: a return the order refuses sends no money at all, checked at the one mock, the card. */
class RefusedReturnAnswerTest {

    private final CardPayments card = mock(CardPayments.class);
    @Test
    void aKettleThatWasNeverBoughtSendsNoMoney() {
        final Refunds refunds = Shop.refunds(Map.of("A-1", Shop.MUG_AND_TEAPOT)::get, card);

        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> refunds.refund("A-1", List.of("kettle")))
                        .withMessage("kettle is not on this order to return"),
                () -> verifyNoInteractions(card));
    }
}
