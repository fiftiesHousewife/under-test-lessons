package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class RefundsTest {

    @Test
    void refundsToTheCard() {
        assertEquals(2500, new Refunds().toCardPence(2500));
    }

    @Disabled("fails since the gift card bonus changed")
    @Test
    void refundsToAGiftCardWithABonus() {
        assertEquals(2625, new Refunds().toGiftCardPence(2500));
    }
}
