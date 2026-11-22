package io.github.fiftieshousewife.undertest.techniques.flaky;

/** Takes orders. The confirmation goes on a background thread, so the customer is never kept waiting for it. */
public final class OrderDesk {

    private final MailServer mail;

    public OrderDesk(final MailServer mail) {
        this.mail = mail;
    }

    /** Takes the order and returns at once; its confirmation is sent a moment later, on its own thread. */
    public void placeOrder(final int order) {
        Thread.ofVirtual().start(() -> mail.send("Order " + order + " confirmed"));
    }
}
