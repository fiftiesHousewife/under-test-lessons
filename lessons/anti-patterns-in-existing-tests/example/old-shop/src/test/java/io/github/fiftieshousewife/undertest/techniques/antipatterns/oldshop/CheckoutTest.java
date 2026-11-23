package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CheckoutTest {

    @Mock
    private Warehouse warehouse;
    @Mock
    private TaxRates taxRates;
    @Mock
    private PaymentGateway gateway;
    @Mock
    private Mailer mailer;
    @Mock
    private AuditLog audit;
    @InjectMocks
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(warehouse.reserve(anyLong())).thenReturn(true);
        when(taxRates.vatPence(1000)).thenReturn(200L);
    }

    @Test
    void takesThePayment() {
        final Order order = checkout.placeOrder(new Basket().add(1000));
        verify(gateway).charge(order.number(), 1200);
    }

    @Test
    void placesTheOrder() {
        assertThat(checkout.placeOrder(new Basket().add(1000))).isNotNull();
    }
}
