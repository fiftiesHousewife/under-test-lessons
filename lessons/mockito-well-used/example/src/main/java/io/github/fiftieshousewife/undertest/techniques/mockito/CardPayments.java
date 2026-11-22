package io.github.fiftieshousewife.undertest.techniques.mockito;

/** The shop's own interface over the card provider's library: the one place money leaves the shop. */
public interface CardPayments {

    /** Sends money back to a card. */
    void refund(Refund refund);
}
