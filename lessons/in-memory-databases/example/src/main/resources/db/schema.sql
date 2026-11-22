-- The shop's invoices, as production's PostgreSQL has them. The application applies this file, and so do the tests.
CREATE TABLE invoices (
    invoice_number INTEGER PRIMARY KEY,
    customer VARCHAR(100) NOT NULL,
    amount_pence BIGINT NOT NULL
);

CREATE INDEX invoices_by_customer ON invoices (customer);
