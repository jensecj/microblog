CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    body TEXT NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by INTEGER REFERENCES users(id)
);
