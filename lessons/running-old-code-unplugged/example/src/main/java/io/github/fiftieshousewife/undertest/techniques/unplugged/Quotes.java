package io.github.fiftieshousewife.undertest.techniques.unplugged;

import org.springframework.stereotype.Service;

/** Quotes a product delivered to a postcode area: its price from the database, its delivery from the courier. */
@Service
public class Quotes {

    private final ProductPrices prices;
    private final DeliveryRates rates;

    public Quotes(final ProductPrices prices, final DeliveryRates rates) {
        this.prices = prices;
        this.rates = rates;
    }

    public Quote quote(final String product, final String postcodeArea) {
        return new Quote(product, prices.pricePence(product), rates.chargePence(postcodeArea));
    }
}
