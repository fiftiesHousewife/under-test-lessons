package io.github.fiftieshousewife.undertest.techniques.architecture.web.today;

import io.github.fiftieshousewife.undertest.techniques.architecture.storage.OrderStore;
import io.github.fiftieshousewife.undertest.techniques.architecture.web.OrderSummary;

/** TODAY: the admin page reads storage directly, skipping the service and its rules. The answer is still right. */
public final class TodaysSummaryPage implements OrderSummary {

    private final OrderStore store;

    public TodaysSummaryPage(final OrderStore store) {
        this.store = store;
    }

    @Override
    public String summary() {
        return "Orders: " + store.count();
    }
}
