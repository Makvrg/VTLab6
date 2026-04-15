CREATE TABLE IF NOT EXISTS vehicle (
    vehicle_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    x INT NOT NULL CHECK (x > -482),
    y BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL CHECK (creation_date <= clock_timestamp() + INTERVAL '5 seconds'),
    engine_power DOUBLE PRECISION NOT NULL CHECK (engine_power > 0),
    distance_travelled FLOAT NOT NULL CHECK (distance_travelled > 0),
    type TEXT NOT NULL,
    fuel_type TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    is_deleted BOOLEAN NOT NULL,

    FOREIGN KEY (user_id)
        REFERENCES "user" (user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
