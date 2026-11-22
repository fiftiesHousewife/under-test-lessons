package io.github.fiftieshousewife.undertest.techniques.blackbox;

import io.github.fiftieshousewife.undertest.techniques.blackbox.fixed.OrderService;
import io.github.fiftieshousewife.undertest.techniques.blackbox.today.TodaysOrderService;
import java.util.Set;

/**
 * The order service the tests start through its entry point: the fix, or today's with -Pcode=today (and with
 * -Pcode=answer, which adds the exercise's answer).
 */
final class Applications {

    private Applications() {
    }

    static ShopApplication start(final Settings settings) {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? TodaysOrderService.start(settings) : OrderService.start(settings);
    }
}
