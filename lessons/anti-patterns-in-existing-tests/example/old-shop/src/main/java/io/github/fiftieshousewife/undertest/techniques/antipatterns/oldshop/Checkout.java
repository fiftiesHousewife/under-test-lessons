package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

/** Places an order: reserves the stock, adds VAT, takes the payment, sends the receipt and records the sale. */
public class Checkout {

    private final Warehouse warehouse;
    private final TaxRates taxRates;
    private final PaymentGateway gateway;
    private final Mailer mailer;
    private final AuditLog audit;

    public Checkout(final Warehouse warehouse, final TaxRates taxRates, final PaymentGateway gateway,
            final Mailer mailer, final AuditLog audit) {
        this.warehouse = warehouse;
        this.taxRates = taxRates;
        this.gateway = gateway;
        this.mailer = mailer;
        this.audit = audit;
    }

    public Order placeOrder(final Basket basket) {
        final long number = OrderNumbers.next();
        if (!warehouse.reserve(number)) {
            throw new IllegalStateException("Out of stock for order " + number);
        }
        final long pence = basket.totalPence() + taxRates.vatPence(basket.totalPence());
        gateway.charge(number, pence);
        mailer.sendReceipt(number, pence);
        audit.sale(number, pence);
        return new Order(number, pence);
    }
}
