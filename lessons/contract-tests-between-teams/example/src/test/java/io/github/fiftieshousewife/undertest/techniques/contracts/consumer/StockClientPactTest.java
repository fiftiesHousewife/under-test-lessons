package io.github.fiftieshousewife.undertest.techniques.contracts.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/*
 * AFTER, the order screen's side: each answer it relies on, written down as an interaction in a pact. Pact stands in
 * for the stock service with exactly these answers, and writes the pact file for the stock service's team to verify.
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "stock-service")
class StockClientPactTest {

    @Pact(consumer = "order-screen")
    V4Pact aKettleInStock(final PactDslWithProvider stockService) {
        return stockService
                .given("three kettles are in stock")
                .uponReceiving("a request for the kettle's stock")
                .path("/stock/KETTLE-01").method("GET")
                .willRespondWith().status(200)
                .body(new PactDslJsonBody()
                        .stringValue("sku", "KETTLE-01")
                        .integerType("available", 3)) // any whole number, named "available"
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "aKettleInStock")
    void readsHowManyAreAvailable(final MockServer stockService) {
        final StockClient client = new StockClient(URI.create(stockService.getUrl()));
        assertThat(client.stockOf("KETTLE-01")).isEqualTo(new StockLevel("KETTLE-01", 3));
    }

    @Pact(consumer = "order-screen")
    V4Pact anItemTheShopDoesNotStock(final PactDslWithProvider stockService) {
        return stockService
                .given("nothing called TOASTER-99 is stocked")
                .uponReceiving("a request for an item the shop doesn't stock")
                .path("/stock/TOASTER-99").method("GET")
                .willRespondWith().status(404)
                .body(new PactDslJsonBody().stringType("error", "no item TOASTER-99"))
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "anItemTheShopDoesNotStock")
    void refusesAnItemTheShopDoesNotStock(final MockServer stockService) {
        final StockClient client = new StockClient(URI.create(stockService.getUrl()));
        assertThatExceptionOfType(NoSuchItemException.class).as("asking for TOASTER-99")
                .isThrownBy(() -> client.stockOf("TOASTER-99")).withMessage("no item TOASTER-99");
    }
}
