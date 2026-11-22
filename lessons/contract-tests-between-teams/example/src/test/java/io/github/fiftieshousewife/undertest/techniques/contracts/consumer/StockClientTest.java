package io.github.fiftieshousewife.undertest.techniques.contracts.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** BEFORE: the order screen's client against a stand-in its own team wrote. It passes, whatever the service sends. */
class StockClientTest {

    private final HandWrittenStockService stockService = new HandWrittenStockService();
    private final StockClient client = new StockClient(stockService.address());

    @AfterEach
    void stop() {
        stockService.close();
    }

    @Test
    void readsHowManyAreAvailable() {
        assertThat(client.stockOf("KETTLE-01")).isEqualTo(new StockLevel("KETTLE-01", 3));
    }
}
