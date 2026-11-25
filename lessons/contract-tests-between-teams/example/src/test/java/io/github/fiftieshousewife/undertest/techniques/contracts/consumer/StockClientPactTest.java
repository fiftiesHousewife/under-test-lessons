package io.github.fiftieshousewife.undertest.techniques.contracts.consumer;

import static org.assertj.core.api.Assertions.assertThat;

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
}
