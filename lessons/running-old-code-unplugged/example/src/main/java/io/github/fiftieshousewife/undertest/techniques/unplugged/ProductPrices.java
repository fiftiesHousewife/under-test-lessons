package io.github.fiftieshousewife.undertest.techniques.unplugged;

import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Reads a product's price, in pence, from the shop's database. */
@Repository
public class ProductPrices {

    private final JdbcTemplate database;

    public ProductPrices(final DataSource shop) {
        this.database = new JdbcTemplate(shop);
    }

    public long pricePence(final String product) {
        final Long price = database.queryForObject(
                "select price_pence from products where code = ?", Long.class, product);
        return Objects.requireNonNull(price, "a product without a price");
    }
}
