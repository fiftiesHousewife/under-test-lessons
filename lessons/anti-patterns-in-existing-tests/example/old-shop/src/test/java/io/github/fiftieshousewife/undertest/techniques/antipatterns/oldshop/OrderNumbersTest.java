package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderNumbersTest {

    @Test
    @Order(1)
    void theFirstOrderIsNumberOne() {
        assertEquals(1, OrderNumbers.next());
    }

    @Test
    @Order(2)
    void theNextOrderIsNumberTwo() {
        assertEquals(2, OrderNumbers.next());
    }
}
