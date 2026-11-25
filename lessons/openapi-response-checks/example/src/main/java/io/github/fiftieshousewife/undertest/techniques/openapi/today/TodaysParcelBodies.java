package io.github.fiftieshousewife.undertest.techniques.openapi.today;

import io.github.fiftieshousewife.undertest.techniques.openapi.Parcel;
import io.github.fiftieshousewife.undertest.techniques.openapi.ParcelBodies;
import java.util.Map;

/** TODAY: sends the weight as text, and the error under "message", which the published schema doesn't allow. */
public final class TodaysParcelBodies implements ParcelBodies {

    @Override
    public Map<String, Object> parcel(final Parcel parcel) {
        return Map.of(
                "id", parcel.id(),
                "status", parcel.status(),
                "weightGrams", String.valueOf(parcel.weightGrams())); // text, as the label printer wanted
    }

    @Override
    public Map<String, Object> noSuchParcel(final String parcelId) {
        return Map.of("message", "no parcel " + parcelId); // renamed from "error" in a tidy-up
    }
}
