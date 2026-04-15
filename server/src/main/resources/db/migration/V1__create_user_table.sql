CREATE TABLE IF NOT EXISTS "user" (
    user_id BIGSERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    hashed_password TEXT NOT NULL,
    salt TEXT NOT NULL,
    is_deleted BOOLEAN NOT NULL
);
