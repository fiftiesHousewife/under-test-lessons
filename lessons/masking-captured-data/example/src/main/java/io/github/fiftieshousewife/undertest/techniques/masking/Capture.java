package io.github.fiftieshousewife.undertest.techniques.masking;

import java.util.List;

/** A day of real data: the shop's orders, and the payments made against them, joined by account number. */
public record Capture(CapturedFile orders, CapturedFile payments) {

    /** Both files, orders first. */
    public List<CapturedFile> files() {
        return List.of(orders, payments);
    }
}
