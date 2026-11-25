package io.github.fiftieshousewife.undertest.techniques.tags;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/* Slow: each test starts the warehouse's test system, which takes 2 seconds. Tagged slow, with @Slow. */
@Slow
class WarehouseStockTest {

    private StockCheck check;

    @BeforeEach
    void startWarehouse() throws InterruptedException {
        check = new StockCheck(WarehouseTestSystem.start(Map.of("Pushchair", 1)));
    }

    @Test
    void sendsTheLastOneOnTheShelf() {
        assertThat(check.canSend("Pushchair", 1)).isTrue();
    }

    @Test
    void refusesAnItemTheWarehouseDoesNotStock() {
        assertThat(check.canSend("Cot", 1)).isFalse();
    }
}
