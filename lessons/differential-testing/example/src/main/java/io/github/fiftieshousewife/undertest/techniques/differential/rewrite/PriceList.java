package io.github.fiftieshousewife.undertest.techniques.differential.rewrite;

import java.util.List;

/** The price list the rewrite reads, typed in from the printed one. */
public final class PriceList {

    public static final List<Band> AS_TYPED_TODAY = List.of( // As typed today: "up to 500 g" was typed as 450.
            new Band(100, 85), new Band(250, 155), new Band(450, 195), new Band(1000, 315));

    public static final List<Band> CORRECTED = List.of( // Checked against the printed list.
            new Band(100, 85), new Band(250, 155), new Band(500, 195), new Band(1000, 315));

    private PriceList() {
    }
}
