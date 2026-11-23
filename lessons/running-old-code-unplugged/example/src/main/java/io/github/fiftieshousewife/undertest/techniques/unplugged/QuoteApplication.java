package io.github.fiftieshousewife.undertest.techniques.unplugged;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** A shop's old quote service: what a product costs, delivered to a postcode, and taking the card payment. */
@SpringBootApplication
public class QuoteApplication {

    public static void main(final String[] args) {
        SpringApplication.run(QuoteApplication.class, args);
    }
}
