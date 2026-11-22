package io.github.fiftieshousewife.undertest.techniques.blackbox;

import io.github.fiftieshousewife.undertest.techniques.blackbox.fixed.OrderService;
import io.github.fiftieshousewife.undertest.techniques.blackbox.today.TodaysOrderService;

/** The order service the tests start through its entry point: the fix, or today's with -Pcode=today. */
final class Applications {

    private Applications() {
    }

    static ShopApplication start(final Settings settings) {
        return "today".equals(System.getProperty("code"))
                ? TodaysOrderService.start(settings) : OrderService.start(settings);
    }
}
