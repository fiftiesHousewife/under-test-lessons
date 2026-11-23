package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

/** Sends the customer's receipt. */
public interface Mailer {

    void sendReceipt(long orderNumber, long pence);
}
