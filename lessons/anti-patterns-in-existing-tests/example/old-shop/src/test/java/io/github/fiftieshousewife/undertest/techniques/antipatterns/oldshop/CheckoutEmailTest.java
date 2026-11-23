package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckoutEmailTest {

    private final Warehouse warehouse = mock(Warehouse.class);
    private final TaxRates taxRates = mock(TaxRates.class);
    private final PaymentGateway gateway = mock(PaymentGateway.class);
    private final Mailer mailer = mock(Mailer.class);
    private final AuditLog audit = mock(AuditLog.class);
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        when(warehouse.reserve(anyLong())).thenReturn(true);
        when(taxRates.vatPence(1000)).thenReturn(200L);
        checkout = new Checkout(warehouse, taxRates, gateway, mailer, audit);
    }

    @Test
    void mailsTheReceipt() {
        final Order order = checkout.placeOrder(new Basket().add(1000));
        verify(mailer).sendReceipt(order.number(), 1200);
    }

    @Test
    void recordsTheSale() {
        final Order order = checkout.placeOrder(new Basket().add(1000));
        verify(audit).sale(order.number(), 1200);
    }
}
