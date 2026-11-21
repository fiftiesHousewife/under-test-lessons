package io.github.fiftieshousewife.undertest.techniques.differential.rewrite;

import java.util.List;

/** The price list the rewrite reads, typed in from the printed one. */
public final class PriceList {

    /** As typed today: "up to 500 g" was typed as 450. */
    public static final List<Band> AS_TYPED_TODAY = List.of(
            new Band(100, 85), new Band(250, 155), new Band(450, 195), new Band(1000, 315));

    /** Checked against the printed list. */
    public static final List<Band> CORRECTED = List.of(
            new Band(100, 85), new Band(250, 155), new Band(500, 195), new Band(1000, 315));

    private PriceList() {
    }
}
