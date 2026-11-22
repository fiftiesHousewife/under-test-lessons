package io.github.fiftieshousewife.undertest.techniques.slices;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** GET /customers/{customer}/deliveries: the customer's deliveries still to come, as JSON. */
@RestController
public class DeliveryController {

    private final DeliveryService service;

    public DeliveryController(final DeliveryService service) {
        this.service = service;
    }

    @GetMapping("/customers/{customer}/deliveries")
    @SuppressFBWarnings(value = "SPRING_ENDPOINT",
            justification = "Reviewed: it only reads, and the customer given only picks which deliveries to list")
    public List<DeliveryAnswer> stillToCome(@PathVariable final String customer) {
        return service.stillToComeFor(customer);
    }
}
