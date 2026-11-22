package io.github.fiftieshousewife.undertest.techniques.cleaning;

import java.util.List;

/** A captured day: the shop's orders, and the payments made against them, joined by account number. */
public record Capture(CapturedFile orders, CapturedFile payments) {

    /** Both files, orders first. */
    public List<CapturedFile> files() {
        return List.of(orders, payments);
    }
}
