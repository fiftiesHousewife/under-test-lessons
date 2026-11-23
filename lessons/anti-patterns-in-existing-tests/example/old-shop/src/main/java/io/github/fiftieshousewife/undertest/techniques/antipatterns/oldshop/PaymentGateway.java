package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

/** The card payment gateway. */
public interface PaymentGateway {

    void charge(long orderNumber, long pence);
}
