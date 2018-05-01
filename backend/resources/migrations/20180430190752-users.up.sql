CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username varchar(20) UNIQUE,
    hash varchar(98) -- size of output from (bh/derive)
);
