package io.github.fiftieshousewife.undertest.techniques.slices;

import java.time.LocalDate;

/**
 * What the service answers for each delivery still to come, written as JSON. The service's published description
 * promises the fields reference, dueOn (an ISO date, such as 2026-11-23) and tracking.
 */
public interface DeliveryAnswer {

    String reference();

    LocalDate dueOn();

    String tracking();
}
