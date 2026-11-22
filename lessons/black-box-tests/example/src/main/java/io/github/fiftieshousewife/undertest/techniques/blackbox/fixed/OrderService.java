package io.github.fiftieshousewife.undertest.techniques.blackbox.fixed;

import io.github.fiftieshousewife.undertest.techniques.blackbox.Settings;
import io.github.fiftieshousewife.undertest.techniques.blackbox.ShopApplication;
import java.nio.file.Path;

/** FIXED: the order service as it ships, keeping orders in tables it commits to. */
public final class OrderService {

    private OrderService() {
    }

    /** Started with the path of its settings file. */
    public static void main(final String[] args) {
        start(Settings.load(Path.of(args[0])));
    }

    public static ShopApplication start(final Settings settings) {
        return ShopApplication.start(settings, OrderTables::new);
    }
}
