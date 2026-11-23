package io.github.fiftieshousewife.undertest.techniques.unplugged;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The service's own tests, unchanged: each starts the whole service and asks it for a price, a delivery charge or a
 * quote. With only the service's settings, they try production; with the test profile, they reach its stand-ins.
 */
@SpringBootTest
class QuotesTest {

    @Autowired
    private ProductPrices prices;

    @Autowired
    private DeliveryRates rates;

    @Autowired
    private Quotes quotes;

    @Test
    void knowsThePriceOfAKettle() {
        assertThat(prices.pricePence("KETTLE")).as("a kettle's price").isEqualTo(2499);
    }

    @Test
    void chargesForDeliveryToCentralLondon() {
        assertThat(rates.chargePence("SW1A")).as("delivery to SW1A").isEqualTo(395);
    }

    @Test
    void quotesAKettleDeliveredToCentralLondon() {
        assertThat(quotes.quote("KETTLE", "SW1A").totalPence()).as("the quote's total").isEqualTo(2894);
    }
}
