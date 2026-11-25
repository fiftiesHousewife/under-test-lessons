package io.github.fiftieshousewife.undertest.techniques.extensions;

/** A line on a delivery note, which goes with the goods: the price with no VAT added, as delivery carries none. */
public interface DeliveryNote {

    String line(String item, long netPence);
}
