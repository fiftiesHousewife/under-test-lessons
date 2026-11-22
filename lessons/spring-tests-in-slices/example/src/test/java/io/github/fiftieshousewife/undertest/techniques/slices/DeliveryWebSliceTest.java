package io.github.fiftieshousewife.undertest.techniques.slices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * AFTER: the web slice. Only the controller and Spring's web layer start; the service is replaced with a Mockito
 * stand-in, so there is no database, no courier and no setting to give. It checks the address, the status and the list.
 */
@WebMvcTest(DeliveryController.class)
class DeliveryWebSliceTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private DeliveryService service;

    @Test
    void answersWithTheCustomersDeliveries() {
        when(service.stillToComeFor("ann"))
                .thenReturn(List.of(Answers.forDelivery(ExampleDeliveries.ovenGloves(), "At the depot")));

        assertThat(mvc.get().uri("/customers/{customer}/deliveries", "ann"))
                .hasStatusOk()
                .bodyJson().extractingPath("$[*].reference").asArray().containsExactly("D-1001");
    }

    @Test
    void answersNotFoundForAnAddressItDoesNotHave() {
        assertThat(mvc.get().uri("/customers/ann/parcels")).hasStatus(404);
    }
}
