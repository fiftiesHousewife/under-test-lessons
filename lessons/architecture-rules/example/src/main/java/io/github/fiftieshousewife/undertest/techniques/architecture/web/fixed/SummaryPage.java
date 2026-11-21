package io.github.fiftieshousewife.undertest.techniques.architecture.web.fixed;

import io.github.fiftieshousewife.undertest.techniques.architecture.service.OrderService;
import io.github.fiftieshousewife.undertest.techniques.architecture.web.OrderSummary;

/** FIXED: the admin page asks the service, as every web class should. */
public final class SummaryPage implements OrderSummary {

    private final OrderService orders;

    public SummaryPage(final OrderService orders) {
        this.orders = orders;
    }

    @Override
    public String summary() {
        return "Orders: " + orders.orderCount();
    }
}
