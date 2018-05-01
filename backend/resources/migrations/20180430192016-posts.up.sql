CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    body varchar(200) NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
