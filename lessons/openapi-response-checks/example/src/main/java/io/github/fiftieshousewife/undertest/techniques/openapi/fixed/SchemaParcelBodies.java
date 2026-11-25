package io.github.fiftieshousewife.undertest.techniques.openapi.fixed;

import io.github.fiftieshousewife.undertest.techniques.openapi.Parcel;
import io.github.fiftieshousewife.undertest.techniques.openapi.ParcelBodies;
import java.util.Map;

/** FIXED: answers as the published schema says: the weight as a whole number, and the error under "error". */
public final class SchemaParcelBodies implements ParcelBodies {

    @Override
    public Map<String, Object> parcel(final Parcel parcel) {
        return Map.of(
                "id", parcel.id(),
                "status", parcel.status(),
                "weightGrams", parcel.weightGrams());
    }

    @Override
    public Map<String, Object> noSuchParcel(final String parcelId) {
        return Map.of("error", "no parcel " + parcelId);
    }
}
