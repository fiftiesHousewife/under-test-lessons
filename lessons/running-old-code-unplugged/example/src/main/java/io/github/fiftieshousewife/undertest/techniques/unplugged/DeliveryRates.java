package io.github.fiftieshousewife.undertest.techniques.unplugged;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Asks the courier's rates service what delivery to a postcode area costs, in pence. */
@Component
public class DeliveryRates {

    private final RestClient courier;

    public DeliveryRates(final RestClient.Builder builder, @Value("${shop.rates.url}") final String address) {
        this.courier = builder.baseUrl(address)
                .requestFactory(new SimpleClientHttpRequestFactory()).build(); // the JDK's HttpURLConnection
    }

    public long chargePence(final String postcodeArea) {
        final Rate rate = courier.get().uri("/rates/{area}", postcodeArea).retrieve().body(Rate.class);
        if (rate == null) {
            throw new IllegalStateException("The courier gave no rate for " + postcodeArea);
        }
        return rate.pence();
    }

    /** The courier's answer: {"area": "SW1A", "pence": 395}. */
    record Rate(String area, long pence) {
    }
}
