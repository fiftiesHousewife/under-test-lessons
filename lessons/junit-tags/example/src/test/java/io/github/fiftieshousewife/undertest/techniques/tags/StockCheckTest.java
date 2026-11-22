package io.github.fiftieshousewife.undertest.techniques.tags;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

/** Quick: the stock check against a warehouse kept in memory, which answers at once. */
class StockCheckTest {

    private final StockCheck check = new StockCheck(item -> Map.of("Car seat", 3).getOrDefault(item, 0));

    @Test
    void sendsWhenThereIsEnoughStock() {
        assertThat(check.canSend("Car seat", 3)).isTrue();
    }

    @Test
    void refusesWhenThereIsNotEnoughStock() {
        assertThat(check.canSend("Car seat", 4)).isFalse();
    }
}
