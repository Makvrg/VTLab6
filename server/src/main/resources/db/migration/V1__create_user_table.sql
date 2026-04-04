CREATE TABLE IF NOT EXISTS "user" (
    user_id BIGSERIAL PRIMARY KEY,
    username TEXT NOT NULL,
    hashed_password TEXT NOT NULL,
    salt TEXT NOT NULL,
    UNIQUE (username)
);
