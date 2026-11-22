package io.github.fiftieshousewife.undertest.techniques.openapi;

import java.util.Map;

/** What the service sends back, as JSON fields: a parcel it knows, or the answer for an id it doesn't. */
public interface ParcelBodies {

    Map<String, Object> parcel(Parcel parcel);

    Map<String, Object> noSuchParcel(String parcelId);
}
