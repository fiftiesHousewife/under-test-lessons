-- The shop's products table, as production has it. Each test application that starts uses the same in-memory database.
create table if not exists products (
    code varchar(20) primary key,
    name varchar(100) not null,
    price_pence bigint not null
);
