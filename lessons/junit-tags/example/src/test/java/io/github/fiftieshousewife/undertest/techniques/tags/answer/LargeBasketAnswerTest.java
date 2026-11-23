package io.github.fiftieshousewife.undertest.techniques.tags.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.fiftieshousewife.undertest.techniques.tags.DeliveryCharge;
import io.github.fiftieshousewife.undertest.techniques.tags.Slow;
import io.github.fiftieshousewife.undertest.techniques.tags.StockCheck;
import io.github.fiftieshousewife.undertest.techniques.tags.WarehouseTestSystem;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * The exercise's answer: a quick test class that gains one test needing the warehouse's test system. Only that test
 * is tagged slow, so the quick task still runs the rest of the class.
 */
class LargeBasketAnswerTest {

    @Test
    void deliveryIsFreeOnALargeBasket() {
        assertThat(DeliveryCharge.forBasket(12_000)).isZero();
    }

    @Test
    @Slow
    void aLargeBasketTheWarehouseCanSendGoesFree() throws InterruptedException {
        final StockCheck check = new StockCheck(WarehouseTestSystem.start(Map.of("Cot", 2)));
        assertAll(
                () -> assertThat(check.canSend("Cot", 2)).isTrue(),
                () -> assertThat(DeliveryCharge.forBasket(12_000)).isZero());
    }
}
