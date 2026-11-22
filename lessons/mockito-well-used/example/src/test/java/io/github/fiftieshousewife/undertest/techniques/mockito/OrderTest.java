package io.github.fiftieshousewife.undertest.techniques.mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

/** What a return is worth: each returned item matched to one line of the order. */
class OrderTest {

    private final Order twoMugsAndAPot = new Order("card-1",
            List.of(new Line("mug", 800), new Line("mug", 800), new Line("teapot", 2500)));

    @Test
    void returningOneOfTwoMugsRefundsOneMug() {
        assertAll(
                () -> assertThat(twoMugsAndAPot.priceOf(List.of("mug"))).isEqualTo(800),
                () -> assertThat(twoMugsAndAPot.priceOf(List.of("mug", "mug", "teapot"))).isEqualTo(4100));
    }

    @Test
    void anItemNotOnTheOrderOrReturnedTooOftenIsRefused() {
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> twoMugsAndAPot.priceOf(List.of("kettle")))
                        .withMessage("kettle is not on this order to return"),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> twoMugsAndAPot.priceOf(List.of("mug", "mug", "mug"))));
    }
}
