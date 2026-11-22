package io.github.fiftieshousewife.undertest.techniques.extensions;

/** A line on an invoice for a trade customer, who sees prices without VAT. */
public interface TradeInvoice {

    String line(String item, long netPence);
}
