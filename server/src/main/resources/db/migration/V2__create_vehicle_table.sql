CREATE TABLE IF NOT EXISTS vehicle (
    vehicle_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    x INT NOT NULL CHECK (x > -482),
    y BIGINT NOT NULL,
    creation_date DATE NOT NULL,
    engine_power DOUBLE PRECISION NOT NULL CHECK (engine_power > 0),
    distance_travelled FLOAT NOT NULL CHECK (distance_travelled > 0),
    type TEXT NOT NULL,
    fuel_type TEXT NOT NULL,
    user_id BIGINT NOT NULL,

    FOREIGN KEY (user_id)
        REFERENCES "user" (user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
