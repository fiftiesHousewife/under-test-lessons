package io.github.fiftieshousewife.undertest.techniques.slices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** A shop's delivery service: customers ask which of their deliveries are still to come. */
@SpringBootApplication
public class DeliveryApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}
