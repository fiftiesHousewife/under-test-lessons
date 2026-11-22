package io.github.fiftieshousewife.undertest.techniques.outsidein;

import com.sun.net.httpserver.HttpHandler;
import io.github.fiftieshousewife.undertest.techniques.outsidein.fixed.BookingHandler;
import io.github.fiftieshousewife.undertest.techniques.outsidein.today.TodaysBookingHandler;
import java.util.Set;

/** The service the tests start: the fixed one, or today's with -Pcode=today (and -Pcode=answer, the exercise's). */
final class Services {

    private Services() {
    }

    static BookingServer start() {
        final HttpHandler handler = Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysBookingHandler() : new BookingHandler();
        return new BookingServer(handler);
    }
}
