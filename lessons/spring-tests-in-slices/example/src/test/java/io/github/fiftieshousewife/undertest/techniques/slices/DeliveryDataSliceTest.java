package io.github.fiftieshousewife.undertest.techniques.slices;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/*
 * AFTER: the data slice. Only Spring Data JPA, the repository and an in-memory H2 database start, and each test's
 * rows are rolled back after it. It checks the query returns the right rows, in the right order.
 */
@DataJpaTest(showSql = false)
class DeliveryDataSliceTest {

    @Autowired
    private TestEntityManager database;

    @Autowired
    private DeliveryRepository repository;

    @Test
    void findsTheDeliveriesStillToComeEarliestFirst() {
        ExampleDeliveries.all().forEach(database::persist);

        assertThat(repository.findByCustomerAndDeliveredFalseOrderByDueOnAsc("ann"))
                .as("Ann's deliveries still to come").extracting(Delivery::getReference)
                .containsExactly("D-1002", "D-1001");
    }
}
