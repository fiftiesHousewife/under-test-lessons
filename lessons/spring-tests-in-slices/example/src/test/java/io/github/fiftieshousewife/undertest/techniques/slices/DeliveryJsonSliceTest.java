package io.github.fiftieshousewife.undertest.techniques.slices;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

/*
 * AFTER: the JSON slice. Only Jackson starts, set up as the application sets it up. It checks the JSON written for one
 * delivery, field by field, as the service's description promises it.
 */
@JsonTest
class DeliveryJsonSliceTest {

    @Autowired
    private JacksonTester<Object> json; // Object, so Jackson writes the record the answer really is

    @Test
    void writesTheFieldsTheDescriptionPromises() throws IOException {
        final DeliveryAnswer answer = Answers.forDelivery(ExampleDeliveries.ovenGloves(), "At the depot");

        assertThat(json.write(answer)).isStrictlyEqualToJson("""
                {"reference": "D-1001", "dueOn": "2026-11-23", "tracking": "At the depot"}""");
    }
}
