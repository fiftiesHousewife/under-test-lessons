package io.github.fiftieshousewife.undertest.techniques.slices;

import java.util.List;
import org.springframework.stereotype.Service;

/** A customer's deliveries still to come, each with what the courier says about it now. */
@Service
public class DeliveryService {

    private final DeliveryRepository deliveries;
    private final CourierClient courier;
    private final DeliveryAnswers answers;

    public DeliveryService(final DeliveryRepository deliveries, final CourierClient courier,
            final DeliveryAnswers answers) {
        this.deliveries = deliveries;
        this.courier = courier;
        this.answers = answers;
    }

    public List<DeliveryAnswer> stillToComeFor(final String customer) {
        return deliveries.findByCustomerAndDeliveredFalseOrderByDueOnAsc(customer).stream()
                .map(delivery -> answers.answer(delivery, courier.trackingFor(delivery.getReference())))
                .toList();
    }
}
