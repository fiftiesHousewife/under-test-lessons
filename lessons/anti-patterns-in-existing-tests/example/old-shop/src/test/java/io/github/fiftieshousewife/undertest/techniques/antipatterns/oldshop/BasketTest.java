package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BasketTest {

    @Test
    void addsUpTheItems() {
        assertEquals(1463, new Basket().add(999).add(464).totalPence());
    }

    @Test
    void takesFivePercentOffFiftyPounds() {
        assertEquals(4750, new Basket().add(5000).totalPence());
    }

    @Test
    void handlesAnEmptyBasket() {
        new Basket().totalPence();
    }
}
