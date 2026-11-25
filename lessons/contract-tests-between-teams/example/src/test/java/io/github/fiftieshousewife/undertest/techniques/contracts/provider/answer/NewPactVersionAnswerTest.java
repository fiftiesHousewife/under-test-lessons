package io.github.fiftieshousewife.undertest.techniques.contracts.provider.answer;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import io.github.fiftieshousewife.undertest.techniques.contracts.provider.Shelves;
import io.github.fiftieshousewife.undertest.techniques.contracts.provider.StockServer;
import io.github.fiftieshousewife.undertest.techniques.contracts.provider.StockServices;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/*
 * THE EXERCISE'S ANSWER: the stock service also verifies version 1.1.0 of the order screen's pact, which adds what the
 * order screen relies on when nobody stocks an item: 404, with an error message. The new state sets up nothing, as an
 * empty shelf is the state. Run it against today's stock service with {@code -Pcode=answer}.
 */
@Provider("stock-service")
@PactFolder("package-repository/order-screen-pacts/1.1.0")
class NewPactVersionAnswerTest {

    private final Shelves shelves = new Shelves();
    private final StockServer service = StockServices.start(shelves);

    @State("nothing called TOASTER-99 is stocked")
    void noToasters() {
        // the shelves start empty, so there is nothing to set up
    }

    @State("three kettles are in stock")
    void threeKettles() {
        shelves.put("KETTLE-01", 3);
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void meetsVersion110(final PactVerificationContext pact) {
        pact.verifyInteraction();
    }

    @BeforeEach
    void pointPactAtTheService(final PactVerificationContext pact) {
        pact.setTarget(new HttpTestTarget("127.0.0.1", service.port()));
    }

    @AfterEach
    void stop() {
        service.close();
    }
}
