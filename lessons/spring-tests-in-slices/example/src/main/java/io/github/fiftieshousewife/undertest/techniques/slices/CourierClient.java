package io.github.fiftieshousewife.undertest.techniques.slices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Asks the courier's tracking service where a parcel is, such as "At the depot". */
@Component
public class CourierClient {

    private final RestClient courier;

    public CourierClient(final RestClient.Builder builder, @Value("${courier.base-url}") final String baseUrl) {
        this.courier = builder.baseUrl(baseUrl).build();
    }

    public String trackingFor(final String reference) {
        final CourierTracking tracking = courier.get().uri("/tracking/{reference}", reference)
                .retrieve().body(CourierTracking.class);
        return tracking == null ? "Not known" : tracking.status();
    }

    /** The courier's answer: {"reference": "D-1001", "status": "At the depot"}. */
    record CourierTracking(String reference, String status) {
    }
}
