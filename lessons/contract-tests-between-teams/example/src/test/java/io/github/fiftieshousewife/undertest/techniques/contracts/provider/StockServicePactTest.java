package io.github.fiftieshousewife.undertest.techniques.contracts.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * AFTER, the stock service's side: every interaction in the order screen's published pact, version 1.0.0, replayed
 * against the real service. The folder stands in for the company's package repository.
 */
@Provider("stock-service")
@PactFolder("package-repository/order-screen-pacts/1.0.0")
class StockServicePactTest {

    private final Shelves shelves = new Shelves();
    private final StockServer service = StockServices.start(shelves);

    @BeforeEach
    void pointPactAtTheService(final PactVerificationContext pact) {
        pact.setTarget(new HttpTestTarget("127.0.0.1", service.port()));
    }

    @AfterEach
    void stop() {
        service.close();
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void meetsThePact(final PactVerificationContext pact) {
        pact.verifyInteraction();
    }

    @State("three kettles are in stock")
    void threeKettles() {
        shelves.put("KETTLE-01", 3);
    }
}
