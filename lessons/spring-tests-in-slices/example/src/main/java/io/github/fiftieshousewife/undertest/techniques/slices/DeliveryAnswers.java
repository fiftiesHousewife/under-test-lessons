package io.github.fiftieshousewife.undertest.techniques.slices;

/** Makes the answer for one delivery, from the database's row and the courier's tracking. */
@FunctionalInterface
public interface DeliveryAnswers {

    DeliveryAnswer answer(Delivery delivery, String tracking);
}
