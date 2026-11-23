-- Two products, made up for the tests.
merge into products (code, name, price_pence) key (code) values ('KETTLE', 'Kettle', 2499);
merge into products (code, name, price_pence) key (code) values ('TOASTER', 'Toaster', 3499);
