package io.github.fiftieshousewife.undertest.techniques.blackbox.today;

import io.github.fiftieshousewife.undertest.techniques.blackbox.Settings;
import io.github.fiftieshousewife.undertest.techniques.blackbox.ShopApplication;
import java.nio.file.Path;

/** TODAY: the order service as it ships today, keeping orders in tables it never commits to. */
public final class TodaysOrderService {

    private TodaysOrderService() {
    }

    /** Started with the path of its settings file. */
    public static void main(final String[] args) {
        start(Settings.load(Path.of(args[0])));
    }

    public static ShopApplication start(final Settings settings) {
        return ShopApplication.start(settings, TodaysOrderTables::new);
    }
}
