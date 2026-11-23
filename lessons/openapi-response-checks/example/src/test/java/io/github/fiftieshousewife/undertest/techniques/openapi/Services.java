package io.github.fiftieshousewife.undertest.techniques.openapi;

import io.github.fiftieshousewife.undertest.techniques.openapi.fixed.DocumentedParcelBodies;
import io.github.fiftieshousewife.undertest.techniques.openapi.today.TodaysParcelBodies;
import java.util.List;

/** The service the tests start, knowing one parcel: the fixed answers, or today's with -Pcode=today. */
public final class Services {

    static final Parcel ON_ITS_WAY = new Parcel("P-1001", "OUT_FOR_DELIVERY", 1200);

    private Services() {
    }

    static ParcelServer start() {
        return new ParcelServer(List.of(ON_ITS_WAY), bodies());
    }

    /** The answers under test: the fixed ones, or today's with -Pcode=today. */
    public static ParcelBodies bodies() {
        return "today".equals(System.getProperty("code")) ? new TodaysParcelBodies() : new DocumentedParcelBodies();
    }
}
