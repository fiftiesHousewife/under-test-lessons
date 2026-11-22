package io.github.fiftieshousewife.undertest.techniques.slices;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** The deliveries table, through Spring Data JPA. */
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    /** A customer's deliveries still to come: not yet delivered, the earliest due first. */
    List<Delivery> findByCustomerAndDeliveredFalseOrderByDueOnAsc(String customer);
}
