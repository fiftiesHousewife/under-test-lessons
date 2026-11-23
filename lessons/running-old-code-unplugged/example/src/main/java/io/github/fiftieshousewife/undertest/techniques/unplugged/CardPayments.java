package io.github.fiftieshousewife.undertest.techniques.unplugged;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Takes an order's card payment through the payment gateway, and keeps the gateway's reference for it. */
@Component
public class CardPayments {

    private final RestClient gateway;

    public CardPayments(final RestClient.Builder builder, @Value("${shop.payments.url}") final String address) {
        this.gateway = builder.baseUrl(address)
                .requestFactory(new SimpleClientHttpRequestFactory()).build(); // the JDK's HttpURLConnection
    }

    public String take(final String order, final long pence) {
        final Payment payment = gateway.post().uri("/payments").body(new PaymentRequest(order, pence))
                .retrieve().body(Payment.class);
        if (payment == null) {
            throw new IllegalStateException("The payment gateway gave no reference for " + order);
        }
        return payment.reference();
    }

    /** What the gateway is sent: {"order": "Q-1001", "pence": 2894}. */
    record PaymentRequest(String order, long pence) {
    }

    /** The gateway's answer: {"reference": "PAY-7781"}. */
    record Payment(String reference) {
    }
}
